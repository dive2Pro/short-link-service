  CREATE TABLE short_links (
      id BIGSERIAL PRIMARY KEY,
      code VARCHAR(16) NOT NULL,
      original_url VARCHAR(2048) NOT NULL,
      created_at TIMESTAMP WITH TIME ZONE NOT NULL,
      CONSTRAINT uk_short_links_code UNIQUE (code)
  );