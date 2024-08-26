# import mysql.connector
# import uuid
# from datetime import datetime

# def populate_wishlist_table(connection):
#     """Populates the wishlist table with sample data."""
#     cursor = connection.cursor()

#     # Fetch user IDs and product IDs
#     cursor.execute("SELECT user_id FROM user limit 20")
#     user_ids = [row[0] for row in cursor]
#     cursor.execute("SELECT product_id FROM product limit 20")
#     product_ids = [row[0] for row in cursor]

#     # Generate sample wishlist data
#     for user_id in user_ids:
#         for product_id in product_ids:
#             wishlist_id = str(uuid.uuid4())
#             created_at = datetime.now()
#             cursor.execute("INSERT INTO wishlist (wishlist_id, user_id, product_id, created_at) VALUES (%s, %s, %s, %s)",
#                            (wishlist_id, user_id, product_id, created_at))

#     connection.commit()
#     cursor.close()
#     print("Wishlist table populated successfully.")

# # Replace with your connection details
# connection = mysql.connector.connect(
#     host="localhost",
#     database="ecommerce",
#     user="root",
#     password="drowssap"
# )

# populate_wishlist_table(connection)
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
        INSERT INTO wishlist (wishlist_id,user_id,product_id,created_at)
        VALUES (%s, %s, %s, %s)
    """
    
    for _, row in data.iterrows():
        record = (
            row['wishlist_id'],
            row['user_id'],
            row['product_id'],
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
        data = load_csv_data('wishlist_data.csv')
        
        if data is not None:
            # Insert data into database
            insert_data_to_database(connection, data)
        
        # Close the connection
        connection.close()

if __name__ == "__main__":
    main()