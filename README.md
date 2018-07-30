<h1>Travelmate</h1>
<p><b>Keywords:</b> Content provider, SQLite database, Cursor Loader, Maps SDK for Android, Places SDK for Android, Location API, Gson library, widget API, Intent service, RemoteViewsFactory, Camera intent, picture resampling, CoordinatorLayout, AppBarLayout, RecyclerView, ViewPager, FragmentAdapter</p>
<p>This application was created as a final project for the Udacity Andorid nanodegree course. It offers the user two main features:</p>
<ol>
<li>To create a custom trip (list of destinations the user wishes to visit).</li>
  <li>To save a random place that is of interest to the user.</li>
</ol>
<p>To create a custom trip, the application uses the Autocomplete Fragment that is a part of the Places SDK. The necessary trip data is stored in the local database as a JSON string. The POJO to JSON/ JSON to POJO conversion is handled by Gson library. Once the trip is created, it appears in the list of trips in the MainActivity - the user can further edit each trip in the detail activity. Possible modifications include: changing the name, deleting destination(s), adding new destination(s).</p>
<p>Each trip is designed as a check list of destinations - to make the interaction easier, the app contains a homescreen widget that shows the seleced trip list - thus, the user may check the destinations on the list directly from the home screen. The communication between the app and the widget is two-way - i.e. every interaction with widget is visible immediately in the main app and vice versa. The communication is handled via IntentService.</p>
<p>To save a favourite location, the app uses the getLastLocation() method from the Location API. However, if the user suspects that the location is not accurate enough (based on the locatiton marker on the map), they may click "My location" button that is a part of the GoogleMap - the button triggers another attempt at locating the device. Of course, the accuracy of the location depends also on the geographical nature of the location-to-be-saved (e.g. It's easier to localize the device in a city than in the wild nature).</p>
<p>For each location, the user may take one picture with their device camera - in the process of creating a new favourite place, the photo is showed only as a thumbnali. The full picture is visible in the place detail activity where it serves also as a cover photo. The photo of a place is not necessary - in case the user decides not to take a picture, the app shows in the detail activity a default picture. Apart from the fullscreen mode, the detail activity offers also the possibility to share location that is shared as a clickable GoogleMaps hyperlink.</p>
<h1>Screenshots</h1>
<table style="font-size:14px;">
<tbody>
<tr>
<td width="25%">
  <img src="https://raw.githubusercontent.com/PavolBriatka/Travelmate/master/screenshots/01_ct_empty_state.png"></td>
<td width="25%">
  <img src="https://raw.githubusercontent.com/PavolBriatka/Travelmate/master/screenshots/02_ct_create_new_trip.png"></td>  
  <td width="25%">
    <img src="https://raw.githubusercontent.com/PavolBriatka/Travelmate/master/screenshots/03_ct_autocomplete_fragment.png"></td>
  <td width="25%">
    <img src="https://raw.githubusercontent.com/PavolBriatka/Travelmate/master/screenshots/04_ct_list_preview.png"></td>
</tr>
  <tr>
<td width="25%">
  <img src="https://raw.githubusercontent.com/PavolBriatka/Travelmate/master/screenshots/05_list_of_trips.png"></td>
<td width="25%">
  <img src="https://raw.githubusercontent.com/PavolBriatka/Travelmate/master/screenshots/06_trip_detail.png"></td>  
  <td width="25%">
    <img src="https://raw.githubusercontent.com/PavolBriatka/Travelmate/master/screenshots/07-trip_on_map.png"></td>
  <td width="25%">
    <img src="https://raw.githubusercontent.com/PavolBriatka/Travelmate/master/screenshots/08_widget.png"></td>
</tr>
  <tr>
<td width="25%">
  <img src="https://raw.githubusercontent.com/PavolBriatka/Travelmate/master/screenshots/09_fp_empty_state.png"></td>
<td width="25%">
  <img src="https://raw.githubusercontent.com/PavolBriatka/Travelmate/master/screenshots/10_fp_add_new_place.png"></td>  
  <td width="25%">
    <img src="https://raw.githubusercontent.com/PavolBriatka/Travelmate/master/screenshots/11_place_detail_no_photo.png"></td>
  <td width="25%">
    <img src="https://raw.githubusercontent.com/PavolBriatka/Travelmate/master/screenshots/12_place_detail_photo.png"></td>
</tr>
  </tbody>
  </table>
