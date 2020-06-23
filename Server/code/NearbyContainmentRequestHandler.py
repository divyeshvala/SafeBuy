import requests
import json

# Author : Kunal Anand
# The code is successfully returning  containment zones in a given radius to a given location

def getContainmentLocations(latitude, longitude, radius):
    print('In function')

    url = 'https://data.geoiq.io/dataapis/v1.0/covid/nearbyzones'

    headers = {'Content-Type': 'application/json'}

    payload = {

        'key': 'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJtYWlsSWRlbnRpdHkiOiJhbmFuZGt1bmFsMjQxQGdtYWlsLmNvbSJ9'
               '.LkOVOiuVPWZdPnSHVs9kpKJCYD2xKnA8sTAfphBcX-E',

        'lng': longitude,

        'lat': latitude,

        'radius': radius

    }

    print(payload["lat"], payload["lng"])

    resp = requests.post(url=url, headers=headers, data=json.dumps(payload))

    result = json.loads(resp.content)

    print(result)

    return result


def handleContainmentrequests(root, tablepath, longitude, latitude, distance):
    response = getContainmentLocations(latitude, longitude, distance)

    root.child(tablepath).push(response)

print(getContainmentLocations(26.91561, 75.76125, 5000))