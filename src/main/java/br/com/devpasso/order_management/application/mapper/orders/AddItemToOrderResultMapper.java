package br.com.devpasso.order_management.application.mapper.orders;

import br.com.devpasso.order_management.application.dto.result.orders.AddItemToOrderResult;
import br.com.devpasso.order_management.domain.model.Order;
import br.com.devpasso.order_management.domain.model.OrderItem;
import org.springframework.stereotype.Component;

@Component
public class AddItemToOrderResultMapper {
    public AddItemToOrderResult map(OrderItem item) {
        return new AddItemToOrderResult(
                item.id(),
                item.productName(),
                item.unitPrice(),
                item.quantity(),
                item.subtotal()
        );
    }
}
