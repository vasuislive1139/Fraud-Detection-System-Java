package com.fraudshield.core.entity;

import jakarta.persistence.*;
import java.util.UUID;
import java.time.LocalDateTime;

@Entity
@Table(name = "case_notes")
public class CaseNote {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id")
    private InvestigationCase investigationCase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    private String content;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public CaseNote() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public InvestigationCase getInvestigationCase() { return investigationCase; }
    public void setInvestigationCase(InvestigationCase investigationCase) { this.investigationCase = investigationCase; }
    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
