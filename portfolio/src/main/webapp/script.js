// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * Adds a random greeting to the page.
 */
function addRandomGreeting() {
  const greetings =
      ['I am form China', 'I love cs', '你好，世界！', 'I love java'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}

async function getAlistOfQuotes(){
  const response = await fetch('/data');
  const quote = await response.text();
  document.getElementById('quote-container').innerText = quote;
}

async function getComments(){
  const response = await fetch('/data');
  const comments = await response.text();
  console.log(typeof comments);
  document.getElementById('comment-container').innerHTML = comments;
}

async function createMap() {
    const uc_berkeley = {lat: 37.8719, lng: -122.2585}
    const map = new google.maps.Map(
      document.getElementById('map'),
      {center: uc_berkeley, 
       zoom: 3,
       styles: [
            {elementType: 'geometry', stylers: [{color: '#242f3e'}]},
            {elementType: 'labels.text.stroke', stylers: [{color: '#242f3e'}]},
            {elementType: 'labels.text.fill', stylers: [{color: '#746855'}]},
            {
              featureType: 'administrative.locality',
              elementType: 'labels.text.fill',
              stylers: [{color: '#d59563'}]
            },
            {
              featureType: 'poi',
              elementType: 'labels.text.fill',
              stylers: [{color: '#d59563'}]
            },
            {
              featureType: 'poi.park',
              elementType: 'geometry',
              stylers: [{color: '#263c3f'}]
            },
            {
              featureType: 'poi.park',
              elementType: 'labels.text.fill',
              stylers: [{color: '#6b9a76'}]
            },
            {
              featureType: 'road',
              elementType: 'geometry',
              stylers: [{color: '#38414e'}]
            },
            {
              featureType: 'road',
              elementType: 'geometry.stroke',
              stylers: [{color: '#212a37'}]
            },
            {
              featureType: 'road',
              elementType: 'labels.text.fill',
              stylers: [{color: '#9ca5b3'}]
            },
            {
              featureType: 'road.highway',
              elementType: 'geometry',
              stylers: [{color: '#746855'}]
            },
            {
              featureType: 'road.highway',
              elementType: 'geometry.stroke',
              stylers: [{color: '#1f2835'}]
            },
            {
              featureType: 'road.highway',
              elementType: 'labels.text.fill',
              stylers: [{color: '#f3d19c'}]
            },
            {
              featureType: 'transit',
              elementType: 'geometry',
              stylers: [{color: '#2f3948'}]
            },
            {
              featureType: 'transit.station',
              elementType: 'labels.text.fill',
              stylers: [{color: '#d59563'}]
            },
            {
              featureType: 'water',
              elementType: 'geometry',
              stylers: [{color: '#17263c'}]
            },
            {
              featureType: 'water',
              elementType: 'labels.text.fill',
              stylers: [{color: '#515c6d'}]
            },
            {
              featureType: 'water',
              elementType: 'labels.text.stroke',
              stylers: [{color: '#17263c'}]
            }
          ]
        });
    const marker = new google.maps.Marker({position: uc_berkeley, map: map});
    //This loop makes the map zoom
    for(i = 3; i<=15;i+=2){
        await timer(1500);
        map.setZoom(i);
    }
    //This will make the street view
    await timer(2000);
    var panorama = map.getStreetView();
    panorama.setPosition({lat: 37.8721883,lng:-122.2584331});
    panorama.setPov(({
    heading: 103,
    pitch: 30
    }));
    panorama.setVisible(true);
}

function timer(ms) {
 return new Promise(res => setTimeout(res, ms));
}

async function changeButton(){
    const response = await fetch('/button');
    const buttonText = await response.text();
    var buttonInfo = buttonText.split(",");
    document.getElementById("login-button").innerText=buttonInfo[0];
    if(buttonInfo[0].length == 8){
        document.getElementById("login-button").style.backgroundColor = "rgb(224, 47, 41)";
    }
    document.getElementById("comment-button").innerText="Submit Comment as "+buttonInfo[1];
}

