package product.controller;


import dto.product.request.ProductCreateRequest;
import dto.product.request.ProductUpdateRequest;
import dto.product.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import product.service.ProductService;
import resposne.BaseResponse;

import java.util.List;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product/v1")
public class ProductController {
    private final ProductService productService;

    /** 전체 조회 혹은 roomId 별 조회 */
    @GetMapping
    public ResponseEntity<BaseResponse<List<ProductResponse>>> getProducts(
            @RequestParam(value = "bandRoomId", required = false) Long bandRoomId
    ) {
        List<ProductResponse> list = (bandRoomId != null)
                ? productService.getProducts(bandRoomId)
                : productService.getProducts(null);  // service 쪽에서 null 처리 로직 필요
        return ResponseEntity.ok(BaseResponse.success(list));
    }

    /** 단일 프로덕트 조회 */
    @GetMapping("/{productId}")
    public ResponseEntity<BaseResponse<?>>  getProduct(
            @PathVariable Long productId
    ) {
        ProductResponse resp = productService.getProduct(productId);
        return ResponseEntity.ok(BaseResponse.success(resp));
    }

    /** 새 프로덕트 생성 */
    @PostMapping
    public ResponseEntity<BaseResponse<?>>  createProduct(
            @RequestBody ProductCreateRequest req
    ) {
        productService.create(req);
        return ResponseEntity.ok(BaseResponse.success());
    }

    /** 프로덕트 전체 업데이트(이름·가격·재고·설명 등) */
    @PutMapping
    public ResponseEntity<BaseResponse<?>> updateProduct(
            @RequestBody ProductUpdateRequest req
    ) {
        productService.updateProduct(req);
        return ResponseEntity.ok(BaseResponse.success());
    }

    /** 단일 프로덕트 삭제 */
    @DeleteMapping("/{productId}")
    public ResponseEntity<BaseResponse<?>> deleteProduct(
            @PathVariable Long productId
    ) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok(BaseResponse.success());
    }

    /** 특정 roomId 에 속한 프로덕트 전부 삭제 */
    @DeleteMapping
    public ResponseEntity<BaseResponse<?>>  deleteAllByRoom(
            @RequestParam Long bandRoomId
    ) {
        productService.deleteAll(bandRoomId);
        return ResponseEntity.ok(BaseResponse.success());
    }

    /** 재고 판매 (quantity 만큼 재고 차감, 실패 시 Bad Request) */
    @PostMapping("/{productId}/sell")
    public ResponseEntity<BaseResponse<?>> sellProduct(
            @PathVariable Long productId,
            @RequestParam Long quantity
    ) {
        boolean ok = productService.sell(productId, quantity);
        return ok
                ? ResponseEntity.badRequest().body(null)
                : ResponseEntity.ok(BaseResponse.success(ok));
    }

    /** 특정 프로덕트의 남은 재고 조회 */
    @GetMapping("/{productId}/stock")
    public ResponseEntity<BaseResponse<?>>  checkStock(
            @PathVariable Long productId
    ) {
        Long stock = productService.checkStock(productId);
        return ResponseEntity.ok(BaseResponse.success(stock));
    }
}