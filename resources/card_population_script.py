# import mysql.connector
# import uuid
# from datetime import datetime, date
# import random

# def generate_random_card_number(card_type):
#     """Generates a random credit card number based on the card type."""
#     prefixes = {
#         "Visa": "4",
#         "Mastercard": "51",
#         "Discover": "6011",
#         "American Express": "34"  # or "37" can be another option
#     }
#     prefix = prefixes[card_type]
#     number = prefix + ''.join(random.choice('0123456789') for _ in range(12))
    
#     check_digit = sum(int(x) * (2 - i % 2) for i, x in enumerate(reversed(number))) % 10
#     check_digit = (10 - check_digit) % 10
    
#     return number + str(check_digit)

# def generate_random_expiration_date(start_year=2024, end_year=2030):
#     """Generates a random expiration date within a specified range."""
#     year = random.randint(start_year, end_year)
#     month = random.randint(1, 12)
    
#     return date(year, month, 1)  # Return the 1st day of the month for expiration

# def populate_cards_table(connection):
#     """Populates the cards table with sample data."""
#     cursor = connection.cursor()

#     # Fetch user ids and names (assuming user_id column exists)
#     cursor.execute("SELECT user_id FROM user")
#     userids = [row[0] for row in cursor]
#     print(userids)

#     def fetch_username(user_id):
#         cursor.execute("SELECT username FROM user WHERE user_id = %s", (user_id,))
#         return cursor.fetchone()[0]

#     # Generate sample card data
#     for user_id in userids:
#         card_holder_name = fetch_username(user_id)  # Assuming user_id for simplicity
#         card_type = random.choice(["Visa", "Mastercard", "Discover", "American Express"])
#         card_number = generate_random_card_number(card_type)
#         expiration_date = generate_random_expiration_date()
#         cvv = random.randint(100, 999)
#         created_at = datetime.now()

#         cursor.execute("""
#             INSERT INTO card (
#                 card_id, 
#                 card_number, 
#                 card_holder_name, 
#                 card_type, 
#                 expiration_date, 
#                 cvv, 
#                 user_id, 
#                 created_at
#             ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
#         """, (
#             str(uuid.uuid4()), 
#             card_number, 
#             card_holder_name, 
#             card_type, 
#             expiration_date, 
#             cvv, 
#             user_id, 
#             created_at
#         ))

#     connection.commit()
#     cursor.close()
#     print("Cards table populated successfully.")

# # Replace with your connection details
# connection = mysql.connector.connect(
#     host="localhost",
#     database="ecommerce",
#     user="root",
#     password="drowssap"
# )

# populate_cards_table(connection)
# connection.close()
# -------------------------------------------------------------------------------------------------------------------------------
import mysql.connector
import uuid
from datetime import datetime
import random
import pandas as pd

def connect_to_database(host, database, user, password):
    """Establish a connection to the MySQL database."""
    try:
        connection = mysql.connector.connect(
            host=host,
            database=database,
            user=user,
            password=password
        )
        if connection.is_connected():
            print(f"Connected to the {database} database")
        return connection
    except Error as e:
        print(f"Error: {e}")
        return None

def load_csv_data(file_path):
    """Load data from CSV file into a pandas DataFrame."""
    try:
        df = pd.read_csv(file_path)
        print("CSV data loaded successfully.")
        return df
    except Exception as e:
        print(f"Error loading CSV: {e}")
        return None

def insert_data_to_database(connection, data):
    """Insert data into the MySQL country table."""
    cursor = connection.cursor()
    insert_query = """
        INSERT INTO card (card_id,card_number,card_holder_name,card_type,expiration_date,cvv,user_id,created_at)
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
    """
    
    for _, row in data.iterrows():
        record = (
            row['card_id'],
            row['card_number'],
            row['card_holder_name'],
            row['card_type'],
            row['expiration_date'],
            row['cvv'],
            row['user_id'],
            row['created_at']
        )
        print(record)
        cursor.execute(insert_query, record)
        print("query executed")
    
    connection.commit()
    cursor.close()
    print("Data inserted successfully.")

def main():
    # Database connection parameters
    host = 'localhost'
    database = 'ecommerce'
    user = 'root'
    password = 'drowssap'
    
    # Connect to the database
    connection = connect_to_database(host, database, user, password)
    
    if connection:
        # Load data from CSV
        data = load_csv_data('card_data.csv')
        
        if data is not None:
            # Insert data into database
            insert_data_to_database(connection, data)
        
        # Close the connection
        connection.close()

if __name__ == "__main__":
    main()