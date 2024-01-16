# Get Pois from Baidu Map with API Key

Important Note: APIs might not get full data, corresponding to your API key limitations.

1. Fill your BaiduMap api_key in `./baidu_pois/spiders/pois.py`:

   ```bash
     self.access_key = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
   ```

2. Get data using API:

   ```bash
   poetry install --no-root
   poetry run scrapy crawl pois
   ```

3. Merge data to single file:

   ```bash
   poetry run python3 ./json_to_csv.py
   ```
