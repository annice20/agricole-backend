package com.agriculture.backend.service;

import com.agriculture.backend.dto.DashboardDTO;

public interface DashboardService {

	DashboardDTO getDashboard(Long regionId); // null = vue nationale (admin)
}