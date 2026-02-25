package com.sp.dataacquisitionprocedure.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "class_log")
public class ClassLog {
    
    @Id
    @Column(name = "id", length = 50)
    private String id;
    
    @Column(name = "student_class", length = 10)
    private String studentClass;
    
    @Column(name = "teacher", length = 50)
    private String teacher;
    
    @Column(name = "course", length = 50)
    private String course;
    
    @Column(name = "course_type", length = 50)
    private String courseType;
    
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "start_date", length = 20)
    private String startDate;
    
    @Column(name = "end_date", length = 20)
    private String endDate;
    
    @Column(name = "update_date")
    private LocalDateTime updateDate;
    
    // Constructors
    public ClassLog() {}
    
    public ClassLog(String id, String studentClass, String teacher, String course, 
                   String courseType, String content, String startDate, 
                   String endDate) {
        this.id = id;
        this.studentClass = studentClass;
        this.teacher = teacher;
        this.course = course;
        this.courseType = courseType;
        this.content = content;
        this.startDate = startDate;
        this.endDate = endDate;
        this.updateDate = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getStudentClass() {
        return studentClass;
    }
    
    public void setStudentClass(String studentClass) {
        this.studentClass = studentClass;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getCourse() {
        return course;
    }
    
    public void setCourse(String course) {
        this.course = course;
    }
    
    public String getTeacher() {
        return teacher;
    }
    
    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }
    
    public String getCourseType() {
        return courseType;
    }
    
    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }
    
    public String getStartDate() {
        return startDate;
    }
    
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    
    public String getEndDate() {
        return endDate;
    }
    
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
    
    public LocalDateTime getUpdateDate() {
        return updateDate;
    }
    
    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }
    
    @Override
    public String toString() {
        return "ClassLog{" +
                "id='" + id + '\'' +
                ", studentClass='" + studentClass + '\'' +
                ", teacher='" + teacher + '\'' +
                ", course='" + course + '\'' +
                ", courseType='" + courseType + '\'' +
                ", content='" + content + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", updateDate=" + updateDate +
                '}';
    }
}