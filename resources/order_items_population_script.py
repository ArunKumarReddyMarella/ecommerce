import mysql.connector
import uuid
from datetime import datetime
import random
import pandas as pd

# def populate_order_items_table(connection):
#     """Populates the order_items table with sample data."""
#     cursor = connection.cursor()

#     # Fetch order IDs and product IDs
#     cursor.execute("SELECT order_id FROM orders")
#     order_ids = [row[0] for row in cursor]
#     cursor.execute("SELECT product_id FROM product limit 20")
#     product_ids = [row[0] for row in cursor]

#     # Generate sample order item data
#     for order_id in order_ids:
#         for product_id in product_ids:
#             order_item_id = str(uuid.uuid4())
#             quantity = random.randint(1, 5)

#             # Fetch product price from the products table
#             cursor.execute("SELECT discounted_price FROM product WHERE product_id = %s", (product_id,))
#             result = cursor.fetchone()
#             if result:
#                 discounted_price = result[0]
#                 price = quantity * discounted_price
#             else:
#                 # Handle case where product_id is not found
#                 price = 0  # Or set a default price

#             cursor.execute("INSERT INTO order_items (order_item_id, order_id, product_id, quantity, price) VALUES (%s, %s, %s, %s, %s)",
#                            (order_item_id, order_id, product_id, quantity, price))

#     connection.commit()
#     cursor.close()
#     print("Order items table populated successfully.")

# # Replace with your connection details
# connection = mysql.connector.connect(
#     host = 'localhost',
#     database = 'ecommerce',
#     user = 'root',
#     password = 'drowssap'
# )
# populate_order_items_table(connection)
# connection.close()

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
        INSERT INTO order_items (order_item_id,order_id,product_id,quantity, price) 
        VALUES (%s, %s, %s, %s, %s)
    """
    
    for _, row in data.iterrows():
        record = (
            row['order_item_id'], 
            row['order_id'], 
            row['product_id'], 
            row['quantity'],
            row['price'],
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
        data = load_csv_data('order_items_data.csv')
        
        if data is not None:
            # Insert data into database
            insert_data_to_database(connection, data)
        
        # Close the connection
        connection.close()

if __name__ == "__main__":
    main()