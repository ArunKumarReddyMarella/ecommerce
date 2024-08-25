import pandas as pd
import mysql.connector
from mysql.connector import Error
from datetime import datetime
import uuid

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

def get_address_uuid(connection, address_id):
    """Fetches the country_uuid from the country table based on country_id."""
    cursor = connection.cursor()
    query = "SELECT address_uuid FROM address WHERE address_id = %s"
    cursor.execute(query, (address_id,))
    result = cursor.fetchone()
    print(address_id)
    print(result)
    cursor.close()
    return result[0] if result else None

def insert_data_to_database(connection, data):
    """Insert data into the MySQL country table."""
    cursor = connection.cursor()
    insert_query = """
        INSERT INTO user (user_id,first_name,last_name,username,email,address_id,created_at,last_update)
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s)

    """
    for _, row in data.iterrows():
        record = (
            row['user_id'], 
            row['first_name'], 
            row['last_name'],
            row['username'],
            row['email'],
            row['address_id'],
            row['created_at'],
            row['last_update'],
        )
        cursor.execute(insert_query, record)
    
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
        data = load_csv_data('user_data.csv')
        
        if data is not None:
            # Insert data into database
            insert_data_to_database(connection, data)
        
        # Close the connection
        connection.close()

if __name__ == "__main__":
    main()
