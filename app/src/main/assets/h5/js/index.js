$.fn.extend({
    Tab:function(ele, list ,opt, callback){
        //参数初始化
        if(!opt) var opt={};
        var _this = this.find(ele);
        _this.click(function(){
            var _idx = $(this).index();
            var _list = $(this).parent().parent().next().find(list).get(_idx);
            $(this).addClass('cur');
            $(this).siblings().removeClass('cur');
            $(_list).fadeIn(200);
            $(_list).siblings().hide();
        })


    }
});

$('#tab1').Tab('.tab ul li','.box');

