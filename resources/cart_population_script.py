import mysql.connector
import uuid
from datetime import datetime
import random
import pandas as pd

# def random_populate_cart_table(connection):
#     """Populates the cart table with sample data."""
#     cursor = connection.cursor()

#     # Fetch user_ids and product_ids from their respective tables
#     cursor.execute("SELECT user_id FROM user limit 20")
#     user_ids = [row[0] for row in cursor]
#     print(user_ids)
#     cursor.execute("SELECT product_id FROM product limit 20")
#     product_ids = [row[0] for row in cursor]
#     print(product_ids)

#     # Generate sample cart data
#     for user_id in user_ids:
#         for product_id in product_ids:
#             cart_id = str(uuid.uuid4())
#             quantity = random.randint(1, 5)  # Adjust quantity range as needed
#             created_at = datetime.now()
#             cursor.execute("INSERT INTO cart (cart_id, user_id, product_id, quantity, created_at) VALUES (%s, %s, %s, %s, %s)",
#                            (cart_id, user_id, product_id, quantity, created_at))

#     connection.commit()
#     cursor.close()
#     print("Cart table populated successfully.")

# # Replace with your connection details
# connection = mysql.connector.connect(
#     host = 'localhost',
#     database = 'ecommerce',
#     user = 'root',
#     password = 'drowssap'
# )



# if __name__ == "__main__":
#     random_populate_cart_table(connection)

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
        INSERT INTO cart (cart_id,user_id,product_id,quantity,created_at)
        VALUES (%s, %s, %s, %s, %s)
    """
    
    for _, row in data.iterrows():
        record = (
            row['cart_id'], 
            row['user_id'], 
            row['product_id'], 
            row['quantity'],
            row['created_at'],
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
        data = load_csv_data('cart_data.csv')
        
        if data is not None:
            # Insert data into database
            insert_data_to_database(connection, data)
        
        # Close the connection
        connection.close()

if __name__ == "__main__":
    main()