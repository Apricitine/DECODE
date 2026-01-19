{
  "startPoint": {
    "x": 56,
    "y": 11.4,
    "heading": "linear",
    "startDeg": 90,
    "endDeg": 180,
    "locked": false
  },
  "lines": [
    {
      "id": "line-z8ex9lsau4",
      "name": "Path 1",
      "endPoint": {
        "x": 46.37667071688942,
        "y": 34.425273390036466,
        "heading": "linear",
        "startDeg": 106,
        "endDeg": 180
      },
      "controlPoints": [],
      "color": "#BDA87D",
      "locked": false,
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": ""
    },
    {
      "id": "mkk0vtr4-g9stfs",
      "name": "Path 2",
      "endPoint": {
        "x": 8,
        "y": 35.17861482381531,
        "heading": "linear",
        "reverse": false,
        "startDeg": 180,
        "endDeg": 182
      },
      "controlPoints": [],
      "color": "#CABB95",
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": ""
    },
    {
      "id": "mkk1326t-in6twi",
      "name": "Path 3",
      "endPoint": {
        "x": 56,
        "y": 11,
        "heading": "linear",
        "reverse": false,
        "startDeg": 180,
        "endDeg": 114
      },
      "controlPoints": [],
      "color": "#CB9ACB",
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": ""
    },
    {
      "id": "mkk17ged-h4fai1",
      "name": "Path 4",
      "endPoint": {
        "x": 24,
        "y": 9,
        "heading": "linear",
        "reverse": false,
        "startDeg": 114,
        "endDeg": 180
      },
      "controlPoints": [],
      "color": "#DA8B8A",
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": ""
    },
    {
      "id": "mkk1ctjp-zz4lcs",
      "name": "Path 5",
      "endPoint": {
        "x": 8,
        "y": 9,
        "heading": "tangential",
        "reverse": false
      },
      "controlPoints": [],
      "color": "#8B6976",
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": ""
    },
    {
      "id": "mkk1haql-1jaj29",
      "name": "Path 6",
      "endPoint": {
        "x": 56,
        "y": 11,
        "heading": "linear",
        "reverse": false,
        "startDeg": 180,
        "endDeg": 114
      },
      "controlPoints": [],
      "color": "#9D77AA",
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": ""
    },
    {
      "id": "mkk3xb3q-o0grhi",
      "name": "Path 7",
      "endPoint": {
        "x": 48,
        "y": 60,
        "heading": "tangential",
        "reverse": false
      },
      "controlPoints": [],
      "color": "#86C59C",
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": ""
    }
  ],
  "shapes": [],
  "sequence": [
    {
      "kind": "path",
      "lineId": "line-z8ex9lsau4"
    },
    {
      "kind": "path",
      "lineId": "mkk0vtr4-g9stfs"
    },
    {
      "kind": "path",
      "lineId": "mkk1326t-in6twi"
    },
    {
      "kind": "wait",
      "id": "mkk160on-kqr14l",
      "name": "Wait",
      "durationMs": 3000,
      "locked": false
    },
    {
      "kind": "path",
      "lineId": "mkk17ged-h4fai1"
    },
    {
      "kind": "path",
      "lineId": "mkk1ctjp-zz4lcs"
    },
    {
      "kind": "wait",
      "id": "mkk1fc15-2sz9hq",
      "name": "Wait",
      "durationMs": 3000,
      "locked": false
    },
    {
      "kind": "path",
      "lineId": "mkk1haql-1jaj29"
    },
    {
      "kind": "wait",
      "id": "mkk1i30y-yx5et4",
      "name": "Wait",
      "durationMs": 2000,
      "locked": false
    },
    {
      "kind": "path",
      "lineId": "mkk3xb3q-o0grhi"
    }
  ],
  "version": "1.2.1",
  "timestamp": "2026-01-18T19:10:48.619Z"
}