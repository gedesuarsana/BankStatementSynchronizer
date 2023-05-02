CREATE TABLE IF NOT EXISTS bank_statement
(
    id numeric(19,0) NOT NULL,
    account_number character varying(255),
    amount numeric(19,2),
    bank character varying(255),
    processed_status character varying(255) ,
    statement character varying(255) ,
    transaction_file_id numeric(19,0),
    transaction_type character varying(255) ,
    transaction_date character varying,
    CONSTRAINT bank_statement_pkey PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS invoice_status
(
    id numeric(19,0) NOT NULL,
    ar_status character varying(255) ,
    bank_statement_id numeric(19,0),
    inquiry_status character varying(255) ,
    invoice_name character varying(255),
    status character varying(255),
    inquiry_amount numeric(19,2),
    remaining_amount numeric(19,2),
    index_in_statement numeric,
    CONSTRAINT invoice_status_pkey PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS transaction
(
    id numeric(19,0) NOT NULL,
    end_time timestamp without time zone,
    file_name character varying(255) ,
    start_time timestamp without time zone,
    status character varying(255),
    CONSTRAINT transaction_pkey PRIMARY KEY (id)
);

CREATE SEQUENCE IF NOT EXISTS hibernate_sequence
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;
