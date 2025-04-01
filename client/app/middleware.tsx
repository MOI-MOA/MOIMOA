// middleware.ts
import { NextResponse } from "next/server";
import type { NextRequest } from "next/server";

export function middleware(request: NextRequest) {
  // 현재 경로
  const path = request.nextUrl.pathname;

  // 공개 경로 (로그인 없이 접근 가능)
  const isPublicPath = path === "/login" || path === "/forgot-password";

  // Authorization 헤더에서 토큰 확인
  const authHeader = request.headers.get("Authorization");
  const token = authHeader?.split(" ")[1] || "";

  // 로그인이 필요한 페이지에 접근하려고 하는데 인증되지 않은 경우
  if (!isPublicPath && !token) {
    // 로그인 페이지로 리다이렉트
    return NextResponse.redirect(new URL("/login", request.url));
  }

  // 이미 로그인한 사용자가 로그인 페이지에 접근하려는 경우
  if (isPublicPath && token) {
    // 메인 페이지로 리다이렉트
    return NextResponse.redirect(new URL("/", request.url));
  }

  // 그 외 경우는 요청 진행
  return NextResponse.next();
}

// 미들웨어를 적용할 경로 지정
export const config = {
  matcher: [
    // 보호된 경로들
    "/group/:path*",
    "/dashboard/:path*",
    "/profile/:path*",
    "/settings/:path*",
    "/",
    "/login",
    "/forgot-password",
  ],
};
