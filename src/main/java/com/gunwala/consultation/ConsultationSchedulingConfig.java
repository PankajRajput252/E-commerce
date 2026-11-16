package com.gunwala.consultation;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/** Turns on @Scheduled (used to release consultation slots held for payments that never arrived). */
@Configuration
@EnableScheduling
class ConsultationSchedulingConfig {
}
