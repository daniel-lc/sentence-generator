CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

create table words (
  word varchar(255) not null,
  word_category varchar(255) not null,
  primary key (word)
);

create table sentences (
  sentence_id uuid not null default uuid_generate_v4(),
  text varchar(255) not null,
  display_count integer not null,
  created timestamp not null,
  primary key (sentence_id)
);


