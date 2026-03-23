$path = "c:\Users\hungn\OneDrive\Desktop\Capstone2\cap2 (1)\cap2\src\main\java\cap2\service\OrderService.java"
$content = Get-Content -Path $path -Raw

$target1 = "List<Order.OrderItem> orderItems = cart.getItems().stream()`r`n                .map(item -> Order.OrderItem.builder()`r`n                        .productId(item.getProductId())`r`n                        .productName(item.getProductName())`r`n                        .productImage(item.getProductImage())`r`n                        .price(item.getPrice())`r`n                        .quantity(item.getQuantity())`r`n                        .subtotal(item.getPrice() * item.getQuantity())`r`n                        .build())`r`n                .toList();"
$repl1 = "List<Order.OrderItem> orderItems = new java.util.ArrayList<>();`r`n        for (cap2.schema.Cart.CartItem item : cart.getItems()) {`r`n            cap2.schema.Product product = productRepository.findById(item.getProductId())`r`n                    .orElseThrow(() -> new AppException(cap2.exception.ErrorCode.PRODUCT_NOT_FOUND));`r`n`r`n            if (product.getStock() != null) {`r`n                if (product.getStock() < item.getQuantity()) {`r`n                    throw new AppException(cap2.exception.ErrorCode.OUT_OF_STOCK);`r`n                }`r`n                product.setStock(product.getStock() - item.getQuantity());`r`n                product.setInStock(product.getStock() > 0);`r`n                productRepository.save(product);`r`n            }`r`n`r`n            orderItems.add(Order.OrderItem.builder()`r`n                    .productId(item.getProductId())`r`n                    .productName(item.getProductName())`r`n                    .productImage(item.getProductImage())`r`n                    .price(item.getPrice())`r`n                    .quantity(item.getQuantity())`r`n                    .subtotal(item.getPrice() * item.getQuantity())`r`n                    .build());`r`n        }"
$content = $content.Replace($target1, $repl1)

$target2 = "order.setStatus(Order.OrderStatus.CANCELLED);`r`n        order.setUpdatedAt(Instant.now());`r`n        Order savedOrder = orderRepository.save(order);"
$repl2 = "order.setStatus(Order.OrderStatus.CANCELLED);`r`n        order.setUpdatedAt(Instant.now());`r`n        Order savedOrder = orderRepository.save(order);`r`n        restoreInventory(savedOrder);"
$content = $content.Replace($target2, $repl2)

$target3 = "order.setStatus(Order.OrderStatus.CANCELLED);`r`n            order.setUpdatedAt(Instant.now());`r`n            Order savedOrder = orderRepository.save(order);"
$repl3 = "order.setStatus(Order.OrderStatus.CANCELLED);`r`n            order.setUpdatedAt(Instant.now());`r`n            Order savedOrder = orderRepository.save(order);`r`n            restoreInventory(savedOrder);"
$content = $content.Replace($target3, $repl3)

Set-Content -Path $path -Value $content
