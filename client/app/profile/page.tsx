"use client";

import { useRouter } from "next/navigation";
import {
  BarChart3,
  User,
  CreditCard,
  ArrowRight,
  Banknote,
  Shield,
  ChevronRight,
} from "lucide-react";
import { Card, CardContent } from "@/components/ui/card";
import { Header } from "@/components/Header";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { useEffect, useState } from "react";
import { authApi } from "@/lib/api";
import { toast } from "@/components/ui/use-toast";
import { Badge } from "@/components/ui/badge";

interface UserInfo {
  id: number;
  name: string;
  email: string;
  profileImage: string;
  joinedGroups: number;
}

export default function ProfilePage() {
  const router = useRouter();
  const [userInfo, setUserInfo] = useState<UserInfo>({
    id: 0,
    name: "",
    email: "",
    profileImage: "/placeholder.svg?height=80&width=80",
    joinedGroups: 0,
  });
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        const response = await authApi.get("/api/v1/profile") as unknown as UserInfo;
        if (response) {
          setUserInfo({
            id: 0,
            name: response.name,
            email: response.email,
            profileImage: "/placeholder.svg?height=80&width=80",
            joinedGroups: response.joinedGroups,
          });
        } else {
          console.error("Invalid API response structure:", response);
          toast({
            title: "오류",
            description: "잘못된 응답 형식입니다.",
            variant: "destructive",
          });
        }
      } catch (error) {
        console.error("사용자 정보 조회 실패:", error);
        toast({
          title: "오류",
          description: "사용자 정보를 불러오는데 실패했습니다.",
          variant: "destructive",
        });
      } finally {
        setIsLoading(false);
      }
    };

    fetchUserInfo();
  }, []);

  const menuItems = [
    {
      title: "통계 현황",
      description: "나의 모임 활동 및 지출 통계",
      icon: <BarChart3 className="h-6 w-6 text-blue-600" />,
      color: "bg-blue-50",
      path: "/profile/statistics",
    },
    {
      title: "마이페이지",
      description: "개인정보 및 계정 설정",
      icon: <User className="h-6 w-6 text-teal-600" />,
      color: "bg-teal-50",
      path: "/profile/mypage",
    },
    {
      title: "자동이체 현황",
      description: "정기 결제 및 자동이체 관리",
      icon: <CreditCard className="h-6 w-6 text-indigo-600" />,
      color: "bg-indigo-50",
      path: "/profile/auto-transfer",
    },
    {
      title: "내 계좌",
      description: "거래내역 보기",
      icon: <Banknote className="h-6 w-6 text-amber-600" />,
      color: "bg-amber-50",
      path: "/profile/account-history",
    },
  ];

  if (isLoading || !userInfo) {
    return (
      <>
        <Header title="내 정보" />
        <main className="flex-1 overflow-auto p-4 space-y-6 pb-16 bg-slate-50">
          <div className="flex items-center justify-center h-64">
            <div className="animate-spin rounded-full h-10 w-10 border-4 border-blue-500 border-t-transparent"></div>
          </div>
        </main>
      </>
    );
  }

  return (
    <>
      <Header title="내 정보" />
      <main className="flex-1 overflow-auto p-4 space-y-6 pb-16 bg-slate-50">
        {/* 사용자 프로필 카드 */}
        <Card className="border-0 shadow-sm rounded-xl overflow-hidden">
          <CardContent className="p-0">
            <div className="bg-gradient-to-r from-blue-600 to-indigo-600 h-32 relative">
              <div className="absolute -bottom-16 left-6">
                <Avatar className="h-32 w-32 border-4 border-white shadow-md">
                  <AvatarImage
                    src={
                      userInfo?.profileImage ||
                      "/placeholder.svg?height=80&width=80"
                    }
                  />
                  <AvatarFallback className="bg-blue-100 text-blue-600 text-3xl">
                    {userInfo?.name?.slice(0, 2) || "??"}
                  </AvatarFallback>
                </Avatar>
              </div>
            </div>
            
            <div className="pt-20 pb-6 px-6">
              <div className="flex justify-between items-start">
                <div>
                  <h2 className="text-2xl font-bold text-slate-800">
                    {userInfo?.name || "사용자"}
                  </h2>
                  <p className="text-sm text-slate-500 mt-1">
                    {userInfo?.email || "이메일 없음"}
                  </p>
                </div>
                <Badge className="bg-blue-100 text-blue-700 hover:bg-blue-200 px-3 py-1 rounded-full">
                  <User className="h-3.5 w-3.5 mr-1.5" />
                  {userInfo?.joinedGroups || 0}개 모임 참여중
                </Badge>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* 메뉴 버튼 */}
        <div className="space-y-3 mt-6">
          <h2 className="text-xl font-semibold text-slate-800 flex items-center mb-3">
            <Shield className="h-5 w-5 mr-2 text-blue-600" />
            사용자 메뉴
          </h2>
          
          {menuItems.map((item, index) => (
            <Card
              key={index}
              className="border-0 shadow-sm hover:shadow-md rounded-xl overflow-hidden transition-all duration-200 cursor-pointer hover:translate-y-[-2px]"
              onClick={() => router.push(item.path)}
            >
              <CardContent className="p-0">
                <div className="flex items-center">
                  <div className={`${item.color} p-6`}>
                    {item.icon}
                  </div>
                  <div className="flex-1 p-4">
                    <h3 className="font-medium text-slate-800">{item.title}</h3>
                    <p className="text-sm text-slate-500 mt-1">
                      {item.description}
                    </p>
                  </div>
                  <div className="pr-4">
                    <ChevronRight className="h-6 w-6 text-slate-400" />
                  </div>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      </main>
    </>
  );
}
