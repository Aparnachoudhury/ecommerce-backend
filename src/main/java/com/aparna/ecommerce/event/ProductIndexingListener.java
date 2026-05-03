package com.aparna.ecommerce.event;
import com.aparna.ecommerce.service.ProductSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ProductIndexingListener {

    private final ProductSearchService productSearchService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onProductUpdated(ProductUpdatedEvent event) {
        System.out.println("🔥 EVENT RECEIVED"); // 👈 HERE

        var p = event.product();
        if (p.isActive()) {
            productSearchService.indexProduct(p);
        } else {
            productSearchService.removeFromIndex(p.getId());
        }
    }
}