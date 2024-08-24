import json
import mysql.connector
from datetime import datetime

def load_json_file(file_path):
    """Load JSON data from a file."""
    with open(file_path, 'r', encoding='utf-8') as file:
        return json.load(file)

def connect_to_database(host,database,user,password):
    """Establish a connection to the MySQL database."""
    return mysql.connector.connect(
        host=host,
        database=database,
        user=user,
        password=password
    )

def parse_product_data(product):
    """Parse and prepare product data for insertion."""
    product_id = product.get('uniq_id')
    crawl_timestamp = datetime.strptime(product.get('crawl_timestamp'), "%Y-%m-%d %H:%M:%S %z")
    product_url = product.get('product_url')
    product_name = product.get('product_name')
    product_category_tree = product.get('product_category_tree')
    pid = product.get('pid')

    retail_price_str = product.get('retail_price')
    retail_price = float(retail_price_str) if retail_price_str else 0

    discounted_price_str = product.get('discounted_price')
    discounted_price = float(discounted_price_str) if discounted_price_str else 0

    images = product.get('image')
    image = json.dumps(json.loads(images)) if images else "[]"

    is_FK_Advantage_product = product.get('is_FK_Advantage_product') == "TRUE"
    description = product.get('description')
    product_rating = product.get('product_rating')
    overall_rating = product.get('overall_rating')
    brand = product.get('brand')
    product_specifications = product.get('product_specifications')

    return (
        product_id, crawl_timestamp, product_url, product_name, product_category_tree, pid,
        retail_price, discounted_price, image, is_FK_Advantage_product, description,
        product_rating, overall_rating, brand, product_specifications,
        0,  # Assuming stock quantity as 0
        'pcs',  # Assuming quantity unit as pieces
        datetime.now()  # Current timestamp for created_at
    )

def insert_product_data(cursor, product_data):
    """Insert a product record into the database."""
    insert_query = """
        INSERT INTO product (
            product_id, crawl_timestamp, product_url, product_name, categories, pid,
            retail_price, discounted_price, image_urls, is_FK_Advantage_product, product_description,
            product_rating, overall_rating, brand, product_specifications, stock_quantity, quantity_unit, created_at
        ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
    """
    cursor.execute(insert_query, product_data)

def process_product_data(file_path):
    """Load, parse, and insert product data into the database."""
    data = load_json_file(file_path)
    host='localhost'
    database='ecommerce'
    user='root'
    password='drowssap'
    connection = connect_to_database(host,database,user,password)
    cursor = connection.cursor()
    
    for count, product in enumerate(data, start=1):
        print(count)
        print(product.get('product_name'))
        product_data = parse_product_data(product)
        insert_product_data(cursor, product_data)

    connection.commit()
    cursor.close()
    connection.close()


if __name__ == "__main__":
    process_product_data('product_data.json')
