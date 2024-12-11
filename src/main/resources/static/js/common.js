//tabMenu01
$(document).ready(function(){
  $('.tabMenu01 a').click(function(){
   $('.tabMenu01 a').removeClass('active');
   $(this).addClass('active');

   var i = $(this).parent().index();
   $('.hobanS_contents > div').fadeOut(0);
   $('.hobanS_contents > div').eq(i).fadeIn(0);

  });
});

//detail_Search
$(document).ready(function(){
  $('.detail').click(function(){
   $('.detailSearch').slideToggle(500);

  });
});