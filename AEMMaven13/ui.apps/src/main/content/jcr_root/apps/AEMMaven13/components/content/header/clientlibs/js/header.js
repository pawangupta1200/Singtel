$(document).ready(function() {

    $.ajax({
         type: 'GET',    
         url:'/content/dam/AEMMaven13/navigation.json',

         success: function(data){
              data= JSON.parse(data);
               var output = '';
               $.each(data.childPages, function(i, item){

            output += '<a href="' + item.properties.pagePath  + '">'  + item.properties.pageTitle+ '</a> &nbsp; &nbsp;';

                     $.each(item.childPages, function(i, childItem){
                      output += '<li>'  + childItem.properties.pageTitle+ '</li> &nbsp; &nbsp;';

                       });
                    output += '</br>';


        });

              $('.headerList').html(output);


         }
     });
  });
 
