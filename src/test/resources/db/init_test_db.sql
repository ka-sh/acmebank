-- Insert account with account number '12345678' and balance 1000000 HKD if it doesn't exist
INSERT INTO accounts (id, number, currency, balance)
SELECT 1, '12345678', 'HKD', 100000000
WHERE NOT EXISTS (SELECT 1 FROM accounts WHERE number = '12345678');

-- Insert account with account number '88888888' and balance 1000000 HKD if it doesn't exist
INSERT INTO accounts (id, number, currency, balance)
SELECT 2, '88888888', 'HKD', 100000000
WHERE NOT EXISTS (SELECT 1 FROM accounts WHERE number = '88888888');
