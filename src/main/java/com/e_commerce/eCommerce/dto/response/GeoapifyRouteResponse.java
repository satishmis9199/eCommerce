package com.e_commerce.eCommerce.dto.response;

import java.util.List;

public record GeoapifyRouteResponse(
        List<Route> results
) {

    public record Route(
            double distance,
            long time
    ) {}
}