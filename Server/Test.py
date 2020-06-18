import requests
import json as js

headers = {'Content-Type': 'application/json'}
data = {
  'key': "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJtYWlsSWRlbnRpdHkiOiJhbmFuZGt1bmFsMjQxQGdtYWlsLmNvbSJ9."
         "LkOVOiuVPWZdPnSHVs9kpKJCYD2xKnA8sTAfphBcX-E",
  "latlngs": [
    [
      22.56904,
      88.359395
    ],
    [
      12.967444,
      77.498775
    ],
    [
      14.656773,
      77.627936
    ],
    [
      19.200027,
      72.970519
    ],
    [
      12.962882,
      77.543543
    ]
  ]
}


response = requests.post(url="https://data.geoiq.io/dataapis/v1.0/covid/locationcheck", data=js.dumps(data), headers=headers)


ch = js.loads(response.content)['data']
print(ch)

