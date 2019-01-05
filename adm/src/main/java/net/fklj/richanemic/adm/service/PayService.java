package net.fklj.richanemic.adm.service;

import net.fklj.richanemic.adm.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PayService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

}
