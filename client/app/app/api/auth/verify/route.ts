// app/api/auth/verify/route.ts
import { NextResponse } from "next/server";
import { cookies } from "next/headers";
import { verifyToken } from "@/lib/auth";

export async function GET() {
  try {
    // 쿠키에서 토큰 가져오기
    const cookieStore = cookies();
    const token = cookieStore.get("auth_token")?.value;

    if (!token) {
      return NextResponse.json({ error: "인증되지 않음" }, { status: 401 });
    }

    // 토큰 검증 및 사용자 정보 가져오기
    const userData = await verifyToken(token);

    if (!userData) {
      return NextResponse.json(
        { error: "유효하지 않은 토큰" },
        { status: 401 }
      );
    }

    // 사용자 정보 반환 (민감한 정보 제외)
    return NextResponse.json({
      id: userData.id,
      name: userData.name,
      email: userData.email,
      // 필요한 사용자 정보만 포함
    });
  } catch (error) {
    console.error("토큰 검증 오류:", error);
    return NextResponse.json({ error: "서버 오류" }, { status: 500 });
  }
}
