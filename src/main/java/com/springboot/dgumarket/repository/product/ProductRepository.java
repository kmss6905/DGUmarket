package com.springboot.dgumarket.repository.product;

import com.drew.lang.annotations.Nullable;
import com.springboot.dgumarket.model.member.Member;
import com.springboot.dgumarket.model.product.Product;
import com.springboot.dgumarket.model.product.ProductCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Created by TK YOUN (2020-12-22 오후 10:06)
 * Github : https://github.com/dgumarket/dgumarket.git
 * Description :
 */
public interface ProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {

    // 인덱스 화면에서 전시될 상품 리스트 입니다.
    // 상품 리스트는 카테고리 별 최대 네 가지 항목을 조회합니다.
    List<Product> findTop4ByProductCategoryOrderByCreateDatetimeDesc(ProductCategory productCategory);

    // 상품 정보가 데이터베이스에 저장된 시점 이후, 해당 상품의 고유 ID를 반환 -> 해당 상품 상세 페이지로 이동시키기 위함
    Product findTopByMemberOrderByCreateDatetimeDesc(Member member);


    // shop, 유저 전체 판매물건 조회 ( p.productStatus = 0 => 삭제가 안된 물건들 )
    @Query("select p from Product p where p.member =:member and p.productStatus = 0")
    List<Product> findAllByMember(Member member, @Nullable Pageable pageable);

    // shop, 유저 특정상태 판매물건 조회 ( p.productStatus = 0 => 삭제가 안된 물건들 )
    @Query("select p from Product p where p.member =:member and p.productStatus = 0 and p.transactionStatusId =:transactionStatusId")
    List<Product> findAllByMemberWithSort(Member member, int transactionStatusId, Pageable pageable);

    // 카테고리별 물건 가져오기 ( feat, 거래중 )
    @Query("select p from Product p where p.productCategory.id =:categoryId and p.transactionStatusId = 0 and p.member.isWithdrawn = 0")
    List<Product> getProductsByCategoryId (int categoryId, @org.springframework.lang.Nullable Pageable pageable);

    // [로그인] 카테고리별 물건 가져오기 ( feat, 거래중, 차단포함)
    // 관심목록 조회하기
    @Query("select p from Product p where " +
            "p.productStatus=0 and p.productCategory.id =:categoryId and " +
            "p.member.isWithdrawn=0 and " +
            "p.member not in (:blockUsers) and p.member not in (:userBlocked)")
    List<Product> getProductByCategoryId (int categoryId, Set<Member> blockUsers, Set<Member> userBlocked, @org.springframework.lang.Nullable Pageable pageable);

    // 전체 물건 가져오기
    @Query("select p from Product p where p.member.isWithdrawn = 0 and p.transactionStatusId =:transactionStatusId")
    List<Product> getAllByTransactionStatusIdEquals(int transactionStatusId, Pageable pageable);

    // 탈퇴유저 제외 2.26
    Optional<Product> findById(int productId);
}