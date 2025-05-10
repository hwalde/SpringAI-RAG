-- Create query_history table
CREATE TABLE query_history (
    id UUID PRIMARY KEY,
    query_text TEXT NOT NULL,
    timestamp TIMESTAMP NOT NULL
);

-- Create index on timestamp for efficient sorting
CREATE INDEX idx_query_history_timestamp ON query_history(timestamp DESC);

-- Add comment to explain the purpose of the table
COMMENT ON TABLE query_history IS 'Stores user query history for display and auto-completion';
