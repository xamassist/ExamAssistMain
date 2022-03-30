package com.ravi.examassistmain.models;

import com.google.gson.annotations.SerializedName;

public class DocumentData {
    @SerializedName("avg_rating")
    private String averageRating;

    @SerializedName("branch")
    private String branch;

    @SerializedName("doc_id")
    private String documentId;

    @SerializedName("doc_type")
    private String documentType;

    @SerializedName("is_premium")
    private int isPremium;

    @SerializedName("paper_year")
    private String paperYear;

    @SerializedName("rating_count")
    private int ratingCount;

    @SerializedName("sub_count_aktu")
    private String subjectCodeAktu;

    @SerializedName("sub_count_mjpru")
    private String subjectCodeMjpru;

    @SerializedName("sub_count")
    private String subjectCode;

    @SerializedName("tag")
    private String documentTag;

    @SerializedName("title")
    private String documentTitle;

    @SerializedName("upload_date")
    private String uploadDate;

    @SerializedName("uploader_id")
    private String uploaderId;

    @SerializedName("view_count")
    private int view_count;

    public DocumentData(String averageRating, String branch, String documentId, String documentType, int isPremium, String paperYear, int ratingCount, String subjectCodeAktu, String subjectCodeMjpru, String subjectCode, String documentTag, String documentTitle, String uploadDate, String uploaderId, int view_count) {
        this.averageRating = averageRating;
        this.branch = branch;
        this.documentId = documentId;
        this.documentType = documentType;
        this.isPremium = isPremium;
        this.paperYear = paperYear;
        this.ratingCount = ratingCount;
        this.subjectCodeAktu = subjectCodeAktu;
        this.subjectCodeMjpru = subjectCodeMjpru;
        this.subjectCode = subjectCode;
        this.documentTag = documentTag;
        this.documentTitle = documentTitle;
        this.uploadDate = uploadDate;
        this.uploaderId = uploaderId;
        this.view_count = view_count;
    }

    public String getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(String averageRating) {
        this.averageRating = averageRating;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public int getIsPremium() {
        return isPremium;
    }

    public void setIsPremium(int isPremium) {
        this.isPremium = isPremium;
    }

    public String getPaperYear() {
        return paperYear;
    }

    public void setPaperYear(String paperYear) {
        this.paperYear = paperYear;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    public String getSubjectCodeAktu() {
        return subjectCodeAktu;
    }

    public void setSubjectCodeAktu(String subjectCodeAktu) {
        this.subjectCodeAktu = subjectCodeAktu;
    }

    public String getSubjectCodeMjpru() {
        return subjectCodeMjpru;
    }

    public void setSubjectCodeMjpru(String subjectCodeMjpru) {
        this.subjectCodeMjpru = subjectCodeMjpru;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getDocumentTag() {
        return documentTag;
    }

    public void setDocumentTag(String documentTag) {
        this.documentTag = documentTag;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getUploaderId() {
        return uploaderId;
    }

    public void setUploaderId(String uploaderId) {
        this.uploaderId = uploaderId;
    }

    public int getView_count() {
        return view_count;
    }

    public void setView_count(int view_count) {
        this.view_count = view_count;
    }
}
/*
@get:PropertyName("avg_rating") @set:PropertyName("avg_rating")
    var averageRating: String = "",

    @get:PropertyName("branch") @set:PropertyName("branch")
    var branch: String = "",

    @get:PropertyName("doc_id") @set:PropertyName("doc_id")
    var documentId: String = "",

    @get:PropertyName("doc_type") @set:PropertyName("doc_type")
    var documentType: Int=0,

    @get:PropertyName("is_premium") @set:PropertyName("is_premium")
    var isPremium: String = "",

    @get:PropertyName("paper_year") @set:PropertyName("paper_year")
    var paperYear: String = "",

    @get:PropertyName("rating_count") @set:PropertyName("rating_count")
    var ratingCount: String = "",

    @get:PropertyName("sub_count_aktu") @set:PropertyName("sub_count_aktu")
    var subject_code_aktu: String = "",

    @get:PropertyName("sub_count_mjpru") @set:PropertyName("sub_count_mjpru")
    var subject_code_mjpru: String = "",

    @get:PropertyName("sub_count") @set:PropertyName("sub_count")
    var subject_code: String = "",

    @get:PropertyName("tag") @set:PropertyName("tag")
    var documentTags: String = "",

    @get:PropertyName("title") @set:PropertyName("title")
    var documentTitle: String,

    @get:PropertyName("upload_date") @set:PropertyName("upload_date")
    var uploadDate: String = "",

    @get:PropertyName("uploader_id") @set:PropertyName("uploader_id")
    var uploaderId: String = "",

    @get:PropertyName("uploader_name") @set:PropertyName("uploader_name")
    var uploaderName: String = "",

    @get:PropertyName("view_count") @set:PropertyName("view_count")
    var viewCount: String,
 */