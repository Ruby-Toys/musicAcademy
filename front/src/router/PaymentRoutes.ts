import PaymentsView from "@/views/payment/PaymentsView.vue";
import PaymentProcessView from "@/views/payment/PaymentProcessView.vue";

export const paymentRoutes = [
    {
        path: "/payments",
        name: "payments",
        component: PaymentsView,
    },
    {
        path: "/paymentProcess",
        name: "paymentProcess",
        component: PaymentProcessView,
    },
]