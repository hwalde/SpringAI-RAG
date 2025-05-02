-- Enable required extensions
CREATE EXTENSION IF NOT EXISTS vector;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Drop existing table if it exists
DROP TABLE IF EXISTS information;

-- Create information table with vector columns
CREATE TABLE information (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title TEXT NOT NULL,
    content TEXT NOT NULL,
    title_embedding VECTOR(768),
    content_embedding VECTOR(768)
);

-- Create HNSW indexes for vector similarity search
CREATE INDEX ON information USING hnsw (title_embedding vector_cosine_ops);
CREATE INDEX ON information USING hnsw (content_embedding vector_cosine_ops);

-- Add comment to explain the purpose of the table
COMMENT ON TABLE information IS 'Stores information with vector embeddings for similarity search';