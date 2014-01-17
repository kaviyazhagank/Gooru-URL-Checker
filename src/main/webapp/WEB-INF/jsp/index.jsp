#-------------------------------------------------------------------------------
#  index.jsp
#  Gooru-URL Checker
#  Created by Gooru on 2014
#  Copyright (c) 2014 Gooru. All rights reserved.
#  http://www.goorulearning.org/
#  Permission is hereby granted, free of charge, to any person      obtaining
#  a copy of this software and associated documentation files (the
#  "Software"), to deal in the Software without restriction, including
#  without limitation the rights to use, copy, modify, merge, publish,
#  distribute, sublicense, and/or sell copies of the Software, and to
#  permit persons to whom the Software is furnished to do so,  subject to
#  the following conditions:
#  The above copyright notice and this permission notice shall be
#  included in all copies or substantial portions of the Software.
#  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY  KIND,
#  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE    WARRANTIES OF
#  MERCHANTABILITY, FITNESS FOR A PARTICULAR  PURPOSE     AND
#  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR  COPYRIGHT HOLDERS BE
#  LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
#  OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
#  WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
#-------------------------------------------------------------------------------
<html>
<head>
<link rel="icon" href="../images/favicon.png" type="image/png">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>JOBS</title>
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css">
<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
<script src="../scripts/jquery.blockUI.js"></script>
<link rel="stylesheet" href="/resources/demos/style.css">
</head>
<body bgcolor="#E6E6FA">
	
<script>
$(document).ready(function(){
$("input#last_updated_input").datepicker({dateFormat: "yy-mm-dd"})

$("#jobStatus").click(function() {
	showBlockUI();
	$.ajax({
		type:'GET',
		url:"resource/counts",
		dataType:"json",
		data: $('#jobStatusForm').serialize(),
		success:function(data){
			var totalCount = data.statusCount.toString();
			if(totalCount.length > 0){
			    $('#count').html('<span style="font-size:25px;color:green">'+ totalCount.length + '</span> Resources Found');
			    $("#Download").show();
			    $("#Download").click(function() {
				$.ajax({
					type:'GET',
					url:"resource/status/filterby",
					dataType:"json",
					data: $('#jobStatusForm').serialize(),
					success:function(data){
						if(data.length > 0){
						   if(typeof data == "undefined" || data == ""){
						  return;
						}
						JSONToCSVConvertor(data, "ResourceDetails", true);
						}
					}
			        });
 
                            });
			} else {
			 $("#Download").hide();
			    $('#count').html('<span style="font-size:25px;color:green">'+'</span> Resource Not Found');
			}
			$.unblockUI();
		},
		
		error:function(data){
			$.unblockUI();
			alert('Sorry! something missing. Please try again later');
		}
		
			
	});
 });
 
});


 function JSONToCSVConvertor(JSONData, ResourceDetails, ShowLabel) {
 
   var arrData = typeof JSONData != 'object' ? JSON.parse(JSONData) : JSONData;
   
    var CSV = '';    
    
    CSV += ResourceDetails + '\r\n\n';
    
    if (ShowLabel) {
        var row = "";
        
        //This loop will extract the label from 1st index of on array
        for (var index in arrData[0]) {
            
            //Now convert each value to string and comma-seprated
       //     row += index + ',';
       row +=[index] + ',';
        }

        row = row.slice(0, row.length-1);
        
        //append Label row with line break
        CSV += row + '\r\n';
    }
    
   for (var i = 0; i < arrData.length; i++) {
        var row = "";
        
        //2nd loop will extract each column and convert it in string comma-seprated
        for (var index in arrData[i]) {
            row += '"' + arrData[i][index] + '",';
        }

        row.slice(0, row.length - 1);
        
        //add a line break after each row
        CSV += row + '\r\n';
    }

    if (CSV == '') {        
        alert("Invalid data");
        return;
    }   
    var fileName = "MyReport_";
   
    fileName += ResourceDetails.replace(/ /g,"_");   
    
    
    var uri = 'data:text/csv;charset=utf-8,' + escape(CSV);
    
    var link = document.createElement("a");    
    link.href = uri;
    
    link.style = "visibility:hidden";
    link.download = fileName + ".csv";
    
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
 }
 
 
 function showBlockUI(){
     $.blockUI({ css: { 
        border: 'none', 
        padding: '15px', 
        backgroundColor: '#000', 
        '-webkit-border-radius': '10px', 
        '-moz-border-radius': '10px', 
                    'border-radius' : '10px',
        opacity: .5, 
        color: '#fff' 
    } }); 
}

</script>
<style>
.downloadLink{
float:left;
}
div.container{
font-family: ProximaNovaRgRegular,'Helvetica Neue',Arial,Helvetica,sans-serif !important;
}
form#jobStatusForm{
hight:1000px;
background-color:#CCCCFF;
font-size:100%;
border: solid silver 1px;
width: 500px;
box-shadow: grey 2px 2px;
position: relative;
top: 75px;
}
select#size_option{
width:175px;
hight:100px;
margin-right:60px;
}
select#sizes_option{
width:175px;
hight:100px;
margin-right:60px;
}
select#size_index{
width:175px;
hight:100px;
margin-right:60px;
}
div.form_key{
width:200px;
hight:100px;
color:#000;
font-size:100%;
margin-left:20px;
float: left;
}
input#last_updated_input{
margin-right:60px;
}
div#last_updated, div#url_status, div#index_done, div#resource_status{
margin-left: 40px;
}
div#jobStatus{
hight:20px;
width:200 px;
margin-left: 40px;
}
div#down{
hight:20px;
width:200 px;
margin-left: 50px;
}
div#form_title{
font-size:130%;
color:green;
font-style:oblique;
font-weight:bold;
}
div#count_details{
position: absolute;
font-size: 17px;
font-style: italic;
right: 225px;
top: 200px;
}
#Download{
display: none;
position: absolute;
right: 267px;
top: 235px;
text-decoration: underline;
cursor: pointer;
}
img#load{
display:none;
position: absolute;
right: 267px;
top: 200px;
}
form
</style>
<div class = "container">
<center>
<form  id="jobStatusForm" bgcolor = "#CCCCFF" >
 <br><div id="form_title" aligh = "center">RESOURCE DETAILS</div></br></br>
 <div id = "last_updated" >
    <div class = "form_key" align = "left"> LastUpdated </div> 
    <div class = "form_value"> <input id ="last_updated_input" type="text" name="lastUpdated" placeholder = "YYYY-MM-DD" /> </div>
 </div></br>
 <div id = "resource_status" >
    <div class = "form_key" align = "left"> ResourceStatus   </div>   
    <div class = "form_value">
    <select id = "sizes_option" name="resourceStatus" >
    <option value="">select...</option>
    <option value="1">Success</option>
    <option value="0">Error</option></select>
  </div> 
 </div>
 <!-- <div id = "url_status" >
    <div class = "form_key" align = "left"> UrlStatus   </div>   
    <div class = "form_value">
    <select id = "size_option" name="urlStatus" >
    <option value="select">select...</option>
    <option value="200">200</option>
    <option value="301">301</option>
    <option value="302">302</option>
    <option value="404">404</option>
    <option value="401">401</option>
    <option value="500">500</option></select>
  </div> 
 </div> --></br>
 <!-- <div id = "index_done" > 
    <div class = "form_key" align = "left"> IndexingStatus </div>
    <div class = "form_value"> 
    <select id = "size_index" name="indexingDone">
    <option value="">select...</option>
    <option value="0">Not Re-indexed</option>
    <option value="1">Re-indexed</option></select>
    </div> 
 </div>-->
 <div class = "form_value"> <input type="button" id="jobStatus" value=" Submit "/>  
 </div></br>
 </center>
 </form>
<div id = "count_details">
  <span class ="count_key"></span>
  <span id = "count"></span>
</div>
<div id = "down">
  <span class = "down_key"></span>
  <div class="downloadLink" id="Download">Download!</div>

</div>
<div id = "load">
<img id="load" src="../images/LoadingWheel.gif" alt="No" width="107" height="98" />
</div>
</body>
</html>
