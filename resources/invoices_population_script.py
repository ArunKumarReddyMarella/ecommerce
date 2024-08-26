# import mysql.connector
# import uuid
# from datetime import datetime

# def populate_invoices_table(connection):
#     """Populates the invoices table with sample data."""
#     cursor = connection.cursor()

#     # Fetch transaction IDs
#     cursor.execute("SELECT transaction_id, amount, transaction_date FROM transactions")
#     transaction_data = cursor.fetchall()

#     # Generate sample invoice data
#     for transaction_id, amount, transaction_data in transaction_data:
#         invoice_id = str(uuid.uuid4())
#         cursor.execute("INSERT INTO invoices (invoice_id, transaction_id, payment_amount, payment_date) VALUES (%s, %s, %s, %s)",
#                        (invoice_id, transaction_id, amount, transaction_data))

#     connection.commit()
#     cursor.close()
#     print("Invoices table populated successfully.")

# # Replace with your connection details
# connection = mysql.connector.connect(
#     host="localhost",
#     database="ecommerce",
#     user="root",
#     password="drowssap"
# )

# populate_invoices_table(connection)
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
        INSERT INTO invoices (invoice_id,transaction_id,payment_amount,payment_date)
        VALUES (%s, %s, %s, %s)
    """
    
    for _, row in data.iterrows():
        record = (
            row['invoice_id'],
            row['transaction_id'],
            row['payment_amount'],
            row['payment_date']
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
        data = load_csv_data('invoice_data.csv')
        
        if data is not None:
            # Insert data into database
            insert_data_to_database(connection, data)
        
        # Close the connection
        connection.close()

if __name__ == "__main__":
    main()