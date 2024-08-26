import mysql.connector
import uuid
from datetime import datetime
import random
import pandas as pd

# def populate_orders_table(connection):
#     """Populates the orders table with sample data."""
#     cursor = connection.cursor()

#     # Fetch user IDs
#     cursor.execute("SELECT user_id FROM user limit 20")
#     user_ids = [row[0] for row in cursor]

#     # Generate sample order data
#     for user_id in user_ids:
#         order_id = str(uuid.uuid4())
#         order_date = datetime.now()
#         total_amount = round(random.uniform(100, 1000), 2)
#         status = random.choice(["pending", "processing", "shipped", "delivered"])
#         cursor.execute("INSERT INTO orders (order_id, user_id, order_date, total_amount, status) VALUES (%s, %s, %s, %s, %s)",
#                        (order_id, user_id, order_date, 0, status))

#     connection.commit()
#     cursor.close()
#     print("Orders table populated successfully.")

# connection = mysql.connector.connect(
#     host = 'localhost',
#     database = 'ecommerce',
#     user = 'root',
#     password = 'drowssap'
# )

# def update_order_total(connection):
#     """Updates the total_amount in the orders table based on order_items."""
#     cursor = connection.cursor()

#     # Prepare the update query
#     update_query = """
#     UPDATE orders
#     SET total_amount = (
#         SELECT SUM(oi.price)
#         FROM order_items oi
#         WHERE oi.order_id = orders.order_id
#     )
#     WHERE orders.order_id = %s;
#     """

#     # Get a list of all order IDs
#     cursor.execute("SELECT order_id FROM orders")
#     order_ids = [row[0] for row in cursor]

#     # Update total_amount for each order
#     for order_id in order_ids:
#         cursor.execute(update_query, (order_id,))

#     connection.commit()
#     cursor.close()
#     print("Order totals updated successfully.")


# # populate_orders_table(connection)
# update_order_total(connection)
# connection.close()
#---------------------------------------------------------------------------------------------------------------------------------------------------

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
        INSERT INTO orders (order_id,user_id,order_date,total_amount,status)
        VALUES (%s, %s, %s, %s, %s)
    """
    
    for _, row in data.iterrows():
        record = (
            row['order_id'], 
            row['user_id'], 
            row['order_date'], 
            row['total_amount'],
            row['status'],
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
        data = load_csv_data('orders_data.csv')
        
        if data is not None:
            # Insert data into database
            insert_data_to_database(connection, data)
        
        # Close the connection
        connection.close()

if __name__ == "__main__":
    main()