/**
 * 提交回复
 */
function post() {
    var questionId = $("#question_id").val();
    var content = $("#comment_content").val();
    comment2target(questionId,1,content);
}

function comment2target(targetId,type,content) {

    if(!content){
        alert("不能回复空内容~~~");
        return;
    }
    $.ajax({
        type: "POST",
        url: "/comment",
        contentType: "application/json",
        data: JSON.stringify({
            "parentId":targetId,
            "content":content,
            "type":type
        }),
        success: function (response){
            if(response.code == 200){
                window.location.reload();
                $("#comment_section").hide();
            }else {
                if(response.code == 2003){
                    var isAccept = confirm(response.message)
                    if(isAccept) {
                        window.open("https://github.com/login/oauth/authorize?client_id=3873f908a02d40e83f6f&redirect_uri=http://localhost:8080/callback&scope=user&state=1");
                        window.localStorage.setItem("closable",true);
                    }
                }else {
                    alert(response.message());
                }
            }
            console.log(response)
        },
        dataType: "json"
    });
}


function comment(e) {
    var commentId = e.getAttribute("data-id");
    var content = $("#input-"+commentId).val();
    comment2target(commentId, 2,content);
}

/**
 * 展开二级评论
 */
function collapseComents(e) {
    var id = e.getAttribute("data-id");
    var comments = $("#comment-" + id);

    //获取一下二级评论的展开状态
    var collapse = e.getAttribute("data-collapse");
    if(collapse) {
        //折叠二级评论
        comments.removeClass("in");

        e.removeAttribute("data-collapse")
        //评论的图标取消高亮
        e.classList.remove("active");
    }else {
        var subCommentContainer = $("#comment-"+id);

         if(subCommentContainer.children().length != 1) {//点击一次
             //展开二级评论
             comments.addClass("in");
             //标记二级评论展开状态
             e.setAttribute("data-collapse","in");
             //将评论的图标点击时高亮
             e.classList.add("active");
         } else {//点击多次，区别：不会使他们重复访问数据库
             $.getJSON("/comment/" + id, function (data) {
                 $.each(data.data.reverse(), function (index, comment) {
                     var mediaLeftElement = $("<div/>", {
                         "class": "media-left"
                     }).append($("<img/>", {
                         "class": "media-object img-rounded",
                         "src": comment.user.avatarUrl
                     }));

                     var mediaBodyElement = $("<div/>", {
                         "class": "media-body"
                     }).append($("<h5/>", {
                         "class": "media-heading",
                         "html": comment.user.name
                     })).append($("<div/>", {
                         "html": comment.content
                     })).append($("<div/>", {
                         "class": "menu"
                     }).append($("<span/>", {
                         "class": "pull-right",
                         "html": moment(comment.gmtCreate).format('YYYY-MM-DD')
                     })));

                     var mediaElement = $("<div/>", {
                         "class": "media"
                     }).append(mediaLeftElement).append(mediaBodyElement);

                     var commentElement = $("<div/>", {
                         "class": "col-lg-12 col-md-12 col-sm-12 col-xs-12 comments"
                     }).append(mediaElement);

                     subCommentContainer.prepend(commentElement);
                 });
                 //展开二级评论
                 comments.addClass("in");
                 //标记二级评论展开状态
                 e.setAttribute("data-collapse","in");
                 //点击评论的图标高亮
                 e.classList.add("active");
             });
         }
    }


}