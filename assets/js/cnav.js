$(function() {

  $('[data-action="cnav"] h1').each(function(){
    var $this = $(this),
    name = $this.html(),
    slug = name.toLowerCase().replace(/ /g, '_'),
    pos = $this.offset().top,
    $item = $('<a></a>')
            .addClass('cnav__item')
            .attr('href', '#'+slug)
            .text(name)
            .on('click', function(e){
                e.preventDefault();
                $('body').animate({scrollTop:pos-24}, '500', 'swing', function() {
              });
            });

    $this.attr('id', slug);

    $('[data-action="sticky"]').append($item);
  })

  $('body').scrollspy({
    target: '.cnav',
    offset: 26
  })

});
