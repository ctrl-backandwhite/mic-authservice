DO $$ BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'users' AND column_name = 'user_name'
    ) THEN
        ALTER TABLE users RENAME COLUMN user_name TO nick_name;
    END IF;
END $$;