# Code for Testing Global ATM Locator API

import requests
import json

url = 'https://sandbox.api.visa.com/globalatmlocator/v1/localatms/atmsinquiry'

headers = {'Accept': 'application/json'}

json_file = open('/home/kunal/PycharmProjects/SafeBuy/Server/Resources/atm_locator_payload.json',)

payload = json.load(json_file)

user_id = 'XB51RTNUVXED4Q8QCL1F21xtGnujtomXrACc_4tXL3WH1ZsdQ'

password = 'AA7AcWiDQ3PLwCmUhflz4rq0MiYw'

key = '/home/kunal/PycharmProjects/SafeBuy/Server/Resources/atm_locator_api_key.pem'

cert = '/home/kunal/PycharmProjects/SafeBuy/Server/Resources/atm_locator_api_cert.pem'

resp = requests.post(url=url, cert=(cert, key), auth=(user_id, password), headers=headers, json=payload, timeout=10)

data = json.loads(resp.content)
print(data)
