ALTER TABLE users
ADD CONSTRAINT chk_user_credits_non_negative
CHECK (survey_credits >= 0);