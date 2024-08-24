import json
import mysql.connector
from datetime import datetime

# Load JSON data from file
with open('product_data.json', 'r', encoding='utf-8') as file:
    data = json.load(file)

# Database connection
connection = mysql.connector.connect(
        host='localhost',
        database='ecommerce',
        user='root',
        password='drowssap'
    )
cursor = connection.cursor()
count = 0
# Insert data into MySQL
for product in data:
    count+=1
    print(count)
    print(product.get('product_name'))
    uniq_id = product.get('uniq_id')
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

    insert_query = """
        INSERT INTO products (
            uniq_id, crawl_timestamp, product_url, product_name, categories, pid,
            retail_price, discounted_price, image_urls, is_FK_Advantage_product, product_description,
            product_rating, overall_rating, brand, product_specifications, stock_quantity, quantity_unit, created_at
        ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
    """
    record = (
        uniq_id, crawl_timestamp, product_url, product_name, product_category_tree, pid,
        retail_price, discounted_price, image, is_FK_Advantage_product, description,
        product_rating, overall_rating, brand, product_specifications,
        0,  # Assuming stock quantity as 0 for this example
        'pcs',  # Assuming quantity unit as pieces for this example
        datetime.now()  # Using current timestamp for created_at
    )

    cursor.execute(insert_query, record)

# Commit and close the connection
connection.commit()
cursor.close()
connection.close()
