$(function () {
   $("#top").click(settop);
    $("#wondelful").click(wondelful);
    $("#delete").click(delete1);

});
function settop(){
    var id=$("#postId").val();
    $.post(
        CONTEXT_PATH+"/discuss/top",
        {"id":id},
        function (data){
            data=$.parseJSON(data);
            if(data.code==0){
                $("#top").attr("disabled","disabled");
            }else {
                alert(data.msg);
            }
        }
    )
}
function wondelful(){
    var id=$("#postId").val();
    $.post(
        CONTEXT_PATH+"/discuss/wondelful",
        {"id":id},
        function (data){
            data=$.parseJSON(data);
            if(data.code==0){
                $("#wondelful").attr("disabled","disabled");
            }else {
                alert(data.msg);
            }
        }
    )
}
function delete1(){
    var id=$("#postId").val();
    $.post(
        CONTEXT_PATH+"/discuss/delete",
        {"id":id},
        function (data){
            data=$.parseJSON(data);
            if(data.code==0){
                location.href=CONTEXT_PATH+"/index";
            }else {
                alert(data.msg);
            }
        }
    )
}

function like(btn,entityType,entityId,entityUserId,postId){
    $.post(
        CONTEXT_PATH+"/like",
        {"entityType":entityType,"entityId":entityId,"entityUserId":entityUserId,"postId":postId},
        function (data){
            data=$.parseJSON(data);
            if(data.code==0){
                $(btn).children("i").text(data.likeCount);
                $(btn).children("b").text(data.likeStatus==1?"已赞":"赞");
            }else {
                alert(data.msg);
            }
        }
    )
}