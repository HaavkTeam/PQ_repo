package org.pqproject.backend.pojo;

import java.util.Date;

//返回给前端的评论对象
public class ReturnComment {
    private String commentId;
    private String questionId;
    private String publisher;
    private String publisherName; // 添加发布者名称
    private String replyId;
    private String replyName; // 添加回复者名称
    private Date time;
    private String content; // 添加评论内容

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getReplyId() {
        return replyId;
    }

    public void setReplyId(String replyId) {
        this.replyId = replyId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getPublisherName() {
        return publisherName;
    }
    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }
    public String getReplyName() {
        return replyName;
    }
    public void setReplyName(String replyName) {
        this.replyName = replyName;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public void createReturnComment(ReturnComment returnComment ,Comment comment, String publisherName, String replyName) {
        returnComment.setCommentId(comment.getCommentId());
        returnComment.setQuestionId(comment.getQuestionId());
        returnComment.setPublisher(comment.getPublisher());
        returnComment.setReplyId(comment.getReplyId());
        returnComment.setTime(comment.getTime());
        returnComment.setPublisherName(publisherName);
        returnComment.setReplyName(replyName);
        returnComment.setContent(comment.getContent());
    }

    public String toString() {
        return "ReturnComment{" +
                "commentId='" + commentId + '\'' +
                ", questionId='" + questionId + '\'' +
                ", publisher='" + publisher + '\'' +
                ", publisherName='" + publisherName + '\'' +
                ", replyId='" + replyId + '\'' +
                ", replyName='" + replyName + '\'' +
                ", time=" + time +
                ", content='" + content + '\'' +
                '}';
    }
}
