package com.vivatech.onlinetutor.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "mumly_tutor_session_meeting")
public class SessionMeeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String joinUrl;
    private String meetingId;
    @Column(name = "host_url", columnDefinition = "TEXT")
    private String hostUrl;

    @OneToOne
    @JoinColumn(name = "tutor_session_id")
    private TutorSession tutorSession;

}
