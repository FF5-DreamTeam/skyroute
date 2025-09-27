//package com.skyroute.skyroute.flight;
//
//import com.skyroute.skyroute.flight.controller.FlightAdminController;
//import com.skyroute.skyroute.flight.dto.admin.FlightResponse;
//import com.skyroute.skyroute.flight.service.admin.FlightService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.data.domain.*;
//import org.springframework.http.ResponseEntity;
//
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.when;
//
//class FlightAdminControllerTest {
//
//    @Mock
//    private FlightService flightService;
//
//    @InjectMocks
//    private FlightAdminController flightAdminController;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testGetFlightsPage_PageStartsAtOne_AdjustsCorrectly() {
//        int page = 1;
//        int size = 5;
//        String[] sort = {"id,asc"};
//
//        Pageable expectedPageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.ASC, "id"));
//
//        FlightResponse mockFlight = new FlightResponse(
//                1L, "SR001", 150, null, null, 299.99, true, null, null, null, null
//        );
//
//        Page<FlightResponse> flightPage =
//                new PageImpl<>(List.of(mockFlight), expectedPageable, 1);
//
//        when(flightService.getFlightsPage(expectedPageable)).thenReturn(flightPage);
//
//        ResponseEntity<Page<FlightResponse>> result =
//                flightAdminController.getFlightsPage(page, size, sort);
//
//        assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
//        assertThat(result.getBody()).isNotNull();
//        assertThat(result.getBody().getContent().get(0).flightNumber()).isEqualTo("SR001");
//    }
//
//    @Test
//    void testGetFlightsPage_DefaultSizeWhenSizeIsZero() {
//        int page = 2;
//        int size = 0;
//        String[] sort = {"id,asc"};
//
//        Pageable effectivePageable = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.ASC, "id"));
//
//        FlightResponse mockFlight = new FlightResponse(
//                3L, "SR003", 200, null, null, 499.99, true, null, null, null, null
//        );
//        Page<FlightResponse> flightPage =
//                new PageImpl<>(List.of(mockFlight), effectivePageable, 1);
//
//        when(flightService.getFlightsPage(effectivePageable)).thenReturn(flightPage);
//
//        ResponseEntity<Page<FlightResponse>> result =
//                flightAdminController.getFlightsPage(page, size, sort);
//
//        assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
//        assertThat(result.getBody()).isNotNull();
//        assertThat(result.getBody().getSize()).isEqualTo(10);
//        assertThat(result.getBody().getContent().get(0).flightNumber()).isEqualTo("SR003");
//    }
//
//    @Test
//    void testGetFlightsPage_SizeAndPageDefaults() {
//        int page = 0;
//        int size = -5;
//        String[] sort = {"id,asc"};
//
//        Pageable effectivePageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
//
//        FlightResponse mockFlight = new FlightResponse(
//                2L, "SR002", 200, null, null, 399.99, true, null, null, null, null
//        );
//        Page<FlightResponse> flightPage =
//                new PageImpl<>(List.of(mockFlight), effectivePageable, 1);
//
//        when(flightService.getFlightsPage(effectivePageable)).thenReturn(flightPage);
//
//        ResponseEntity<Page<FlightResponse>> result =
//                flightAdminController.getFlightsPage(page, size, sort);
//
//        assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
//        assertThat(result.getBody()).isNotNull();
//        assertThat(result.getBody().getSize()).isEqualTo(10);
//        assertThat(result.getBody().getContent().get(0).flightNumber()).isEqualTo("SR002");
//    }
//}