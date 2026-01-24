package pl.gdansk.cinema.cinema_booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.gdansk.cinema.cinema_booking.service.StatisticsService;

@Controller
@RequestMapping("/admin/statystyki")
@RequiredArgsConstructor
public class AdminStatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping
    public String showStatistics(Model model) {
        model.addAttribute("salesStats", statisticsService.getSalesStatistics());
        model.addAttribute("occupancyStats", statisticsService.getSeansOccupancyReport());
        return "admin/statystyki";
    }
}
