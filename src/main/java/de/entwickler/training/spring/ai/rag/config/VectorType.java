package de.entwickler.training.spring.ai.rag.config;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;
import org.postgresql.util.PGobject;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Custom Hibernate type for PostgreSQL vector type using PGobject.
 * This allows JPA to work with pgvector's vector type via its string representation.
 */
public class VectorType implements UserType<float[]> {

    @Override
    public int getSqlType() {
        // Use OTHER as vector is a custom type
        return Types.OTHER;
    }

    @Override
    public Class<float[]> returnedClass() {
        return float[].class;
    }

    @Override
    public boolean equals(float[] x, float[] y) {
        return Arrays.equals(x, y); // Use Arrays.equals for array comparison
    }

    @Override
    public int hashCode(float[] x) {
        return Arrays.hashCode(x); // Use Arrays.hashCode for array hashing
    }

    @Override
    public float[] nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) throws SQLException {
        // Try reading as PGobject first, fallback to String
        Object object = rs.getObject(position);

        if (rs.wasNull() || object == null) {
            return null;
        }

        String vectorString;
        if (object instanceof PGobject pgObject) {
            if (!"vector".equalsIgnoreCase(pgObject.getType())) {
                throw new HibernateException("Expected PGobject of type 'vector' but got '" + pgObject.getType() + "'");
            }
            vectorString = pgObject.getValue();
        } else if (object instanceof String) {
            vectorString = (String) object;
        } else {
            throw new HibernateException("Unexpected object type for vector: " + object.getClass().getName());
        }


        if (vectorString == null || vectorString.isEmpty() || !vectorString.startsWith("[") || !vectorString.endsWith("]")) {
            throw new HibernateException("Invalid vector string format: " + vectorString);
        }

        // Parse the string "[f1,f2,f3]"
        String[] elements = vectorString.substring(1, vectorString.length() - 1).split(",");
        float[] result = new float[elements.length];
        try {
            for (int i = 0; i < elements.length; i++) {
                result[i] = Float.parseFloat(elements[i].trim());
            }
        } catch (NumberFormatException e) {
            throw new HibernateException("Failed to parse float from vector string: " + vectorString, e);
        }
        return result;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, float[] value, int index, SharedSessionContractImplementor session) throws SQLException {
        if (value == null) {
            // Explicitly set the type name for null values if required by the driver/db
            st.setNull(index, Types.OTHER, "vector");
            // Alternatively, some drivers might prefer just Types.OTHER or Types.NULL
            // st.setNull(index, Types.OTHER);
        } else {
            // Format the float array as "[f1,f2,f3]"
            Stream<Float> floatObjectStream = IntStream.range(0, value.length)
                    .mapToObj(i -> value[i]);

            String vectorString = floatObjectStream
                    .map(String::valueOf)
                    .collect(Collectors.joining(",", "[", "]"));

            PGobject pgObject = new PGobject();
            pgObject.setType("vector"); // Set the PostgreSQL type name
            pgObject.setValue(vectorString);
            st.setObject(index, pgObject, Types.OTHER); // Use setObject with Types.OTHER
        }
    }

    @Override
    public float[] deepCopy(float[] value) {
        if (value == null) {
            return null;
        }
        return Arrays.copyOf(value, value.length); // Use Arrays.copyOf for deep copy
    }

    @Override
    public boolean isMutable() {
        // float[] is technically mutable, so return true.
        return true;
    }

    @Override
    public Serializable disassemble(float[] value) {
        // deepCopy is sufficient for disassembly as float[] is Serializable
        return deepCopy(value);
    }

    @Override
    public float[] assemble(Serializable cached, Object owner) {
        // deepCopy is sufficient for assembly
        if (cached == null) {
            return null;
        }
        if (!(cached instanceof float[])) {
            throw new HibernateException("Cached value is not a float[]: " + cached.getClass().getName());
        }
        return deepCopy((float[]) cached);
    }

    @Override
    public float[] replace(float[] detached, float[] managed, Object owner) {
        // Return a deep copy of the detached value
        return deepCopy(detached);
    }
}