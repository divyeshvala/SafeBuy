# Code for Testing Global ATM Locator API

import requests
import json

url = 'https://sandbox.api.visa.com/globalatmlocator/v1/localatms/atmsinquiry'

headers = {'Accept': 'application/json'}

payload = json.loads(''' 
{
    "wsRequestHeaderV2": {
        "requestTs": "2020-06-17T05:57:46.000Z",
        "applicationId": "VATMLOC",
        "requestMessageId": "ICE01-001",
        "userId": "CDISIUserID",
        "userBid": "10000108",
        "correlationId": "909420141104053819418"
    },
    "requestData": {
        "culture": "en-US",
        "distance": "20",
        "distanceUnit": "km",
        "metaDataOptions": 0,
        "location": {
        "address": null,
        "placeName": "vile parley, Mumbai",
        "geocodes": null
    },
    "options": {
        "range": {
        "start": 10,
        "count": 20
        },
    "sort": {
    "primary": "city",
    "direction": "asc"
    },
"operationName": "or",
"findFilters": [
{
"filterName": "OPER_HRS",
"filterValue": "C"
},
{
"filterName": "AIRPORT_CD",
"filterValue": ""
},
{
"filterName": "WHEELCHAIR",
"filterValue": "N"
},
{
"filterName": "BRAILLE_AUDIO",
"filterValue": "N"
},
{
"filterName": "BALANCE_INQUIRY",
"filterValue": "N"
},
{
"filterName": "CHIP_CAPABLE",
"filterValue": "N"
},
{
"filterName": "PIN_CHANGE",
"filterValue": "N"
},
{
"filterName": "RESTRICTED",
"filterValue": "N"
},
{
"filterName": "PLUS_ALLIANCE_NO_SURCHARGE_FEE",
"filterValue": "N"
},
{
"filterName": "ACCEPTS_PLUS_SHARED_DEPOSIT",
"filterValue": "N"
},
{
"filterName": "V_PAY_CAPABLE",
"filterValue": "N"
},
{
"filterName": "READY_LINK",
"filterValue": "N"
}
],
"useFirstAmbiguous": true
}
}
}''')

user_id = 'XB51RTNUVXED4Q8QCL1F21xtGnujtomXrACc_4tXL3WH1ZsdQ'

password = 'AA7AcWiDQ3PLwCmUhflz4rq0MiYw'

key = '/home/kunal/Desktop/VISA_APIs/atm_locator_test/key_8cab41a5-6a66-4636-9d11-a8587ead4d99.pem'

cert = '/home/kunal/Desktop/VISA_APIs/atm_locator_test/cert.pem'

resp = requests.post(url=url, cert=(cert, key), auth=(user_id, password), headers=headers, json=payload, timeout=10)

data = json.loads(resp.content)
print(data)
