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

def clean_data(row):
    """Clean and validate the row data."""
    row['secondary_address'] = row['secondary_address'] if pd.notna(row['secondary_address']) else None
    row['postal_code'] = row['postal_code'] if pd.notna(row['postal_code']) and row['postal_code'] else None
    row['phone'] = row['phone'] if pd.notna(row['phone']) and row['phone'] else None
    row['district'] = row['district'] if pd.notna(row['district']) and row['district'] else None
    
    # Ensure location is valid WKT; if not, set it to None
    row['location'] = row['location'] if pd.notna(row['location']) and 'POINT' in row['location'] else None

    # Ensure the last_update is in the correct datetime format
    try:
        row['last_update'] = pd.to_datetime(row['last_update'])
    except Exception:
        row['last_update'] = datetime.now()

    return row

def insert_data_to_database(connection, data):
    """Insert data into the MySQL address table."""
    cursor = connection.cursor()

    insert_query = """
        INSERT INTO address (
            address_id,
            primary_address, 
            secondary_address, 
            district, 
            city_id, 
            postal_code, 
            phone, 
            location, 
            last_update
        ) VALUES (%s, %s, %s, %s, %s, %s, %s, ST_GeomFromText(%s), %s)
    """

    for _, row in data.iterrows():
        cleaned_row = clean_data(row)
        
        record = (
            cleaned_row['address_uuid'],
            cleaned_row['primary_address'],
            cleaned_row['secondary_address'],
            cleaned_row['district'],
            cleaned_row['city_id'],
            cleaned_row['postal_code'],
            cleaned_row['phone'],
            cleaned_row['location'],  # Assuming 'location' column contains WKT (Well-Known Text) for geometry
            cleaned_row['last_update'],
        )
        
        try:
            cursor.execute(insert_query, record)
        except Error as e:
            print(f"Error inserting data: {e}")
    
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
        data = load_csv_data('address_data.csv')
        
        if data is not None:
            # Insert data into database
            insert_data_to_database(connection, data)
        
        # Close the connection
        connection.close()

if __name__ == "__main__":
    main()
