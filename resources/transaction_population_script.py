# import mysql.connector
# import uuid
# from datetime import datetime
# import random

# def populate_transactions_table(connection):
#     """Populates the transactions table with sample data."""
#     cursor = connection.cursor()

#     # Fetch order IDs
#     cursor.execute("SELECT order_id FROM orders")
#     order_ids = [row[0] for row in cursor]

#     # Generate sample transaction data
#     for order_id in order_ids:
#         # Fetch card_id from the cards table using the user_id from the order
#         cursor.execute("SELECT card_id FROM card WHERE user_id = (SELECT user_id FROM orders WHERE order_id = %s)", (order_id,))
#         card_id_result = cursor.fetchone()
#         if card_id_result:
#             card_id = card_id_result[0]
#         else:
#             # Handle case where card_id is not found
#             card_id = None  # Or set a default value

#         transaction_id = str(uuid.uuid4())
#         transaction_date = datetime.now()
#         cursor.execute("select total_amount from orders where order_id = %s",(order_id,))
#         total_amount = cursor.fetchone()[0]
#         cursor.execute("INSERT INTO transactions (transaction_id, order_id, card_id, amount, transaction_date) VALUES (%s, %s, %s, %s, %s)",
#                        (transaction_id, order_id, card_id, total_amount, transaction_date))

#     connection.commit()
#     cursor.close()
#     print("Transactions table populated successfully.")

# # Replace with your connection details
# connection = mysql.connector.connect(
#     host="localhost",
#     database="ecommerce",
#     user="root",
#     password="drowssap"
# )

# populate_transactions_table(connection)
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
        INSERT INTO transactions (transaction_id,order_id,card_id,amount,transaction_date)
        VALUES (%s, %s, %s, %s, %s)
    """
    
    for _, row in data.iterrows():
        record = (
            row['transaction_id'],
            row['order_id'],
            row['card_id'],
            row['amount'],
            row['transaction_date']
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
        data = load_csv_data('transaction_data.csv')
        
        if data is not None:
            # Insert data into database
            insert_data_to_database(connection, data)
        
        # Close the connection
        connection.close()

if __name__ == "__main__":
    main()