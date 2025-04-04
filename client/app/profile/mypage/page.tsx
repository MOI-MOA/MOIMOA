"use client";

import { useRouter } from "next/navigation";
import { Header } from "@/components/Header";
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
  CardDescription,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Switch } from "@/components/ui/switch";
import { Edit, User, Shield, LogOut } from "lucide-react";
import { useState, useEffect } from "react";
import { toast } from "@/components/ui/use-toast";
import { authApi } from "@/lib/api";
import { useAuth } from "@/app/context/AuthContext";

interface UserInfo {
  name: string;
  email: string;
  birth: string;
  profileImage: string;
  notifications: {
    email: boolean;
    push: boolean;
    sms: boolean;
  };
}

export default function MyPage() {
  const router = useRouter();
  const { logout } = useAuth();
  const [isLoading, setIsLoading] = useState(true);
  const [userInfo, setUserInfo] = useState<UserInfo>({
    name: "",
    email: "",
    birth: "",
    profileImage: "/placeholder.svg?height=100&width=100",
    notifications: {
      email: true,
      push: true,
      sms: false,
    },
  });

  useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        const response = await authApi.get("/api/v1/profile/mypage");
        if (response) {
          setUserInfo({
            name: response.name,
            email: response.email,
            birth: response.birth || "",
            profileImage:
              response.profileImage || "/placeholder.svg?height=100&width=100",
            notifications: {
              email: true,
              push: true,
              sms: false,
            },
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

  const handleNotificationChange = (type: string, checked: boolean) => {
    setUserInfo((prev) => ({
      ...prev,
      notifications: {
        ...prev.notifications,
        [type]: checked,
      },
    }));

    toast({
      title: `${
        type === "email" ? "이메일" : type === "push" ? "푸시" : "SMS"
      } 알림 ${checked ? "활성화" : "비활성화"}`,
      description: `${
        type === "email" ? "이메일" : type === "push" ? "푸시" : "SMS"
      } 알림이 ${checked ? "활성화" : "비활성화"} 되었습니다.`,
    });
  };

  if (isLoading) {
    return (
      <>
        <Header title="마이페이지" showBackButton />
        <main className="flex-1 overflow-auto p-4 space-y-6 pb-16">
          <div className="flex items-center justify-center h-64">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
          </div>
        </main>
      </>
    );
  }

  return (
    <>
      <Header title="마이페이지" showBackButton />
      <main className="flex-1 overflow-auto p-4 space-y-6 pb-16">
        <Card>
          <CardHeader>
            <CardTitle>프로필 정보</CardTitle>
            <CardDescription>개인 정보를 확인하고 관리하세요</CardDescription>
          </CardHeader>
          <CardContent className="space-y-6">
            <div className="flex flex-col items-center mb-4">
              <Avatar className="h-24 w-24 mb-2">
                <AvatarImage src={userInfo.profileImage} />
                <AvatarFallback>{userInfo.name.slice(0, 2)}</AvatarFallback>
              </Avatar>
            </div>

            <div className="space-y-4">
              <div className="flex justify-between items-center border-b pb-2">
                <div className="space-y-1">
                  <p className="text-sm text-gray-500">이름</p>
                  <p className="font-medium">{userInfo.name}</p>
                </div>
              </div>

              <div className="flex justify-between items-center border-b pb-2">
                <div className="space-y-1">
                  <p className="text-sm text-gray-500">이메일</p>
                  <p className="font-medium">{userInfo.email}</p>
                </div>
              </div>
            </div>

            <Button
              className="w-full"
              onClick={() =>
                router.push(
                  `/profile/mypage/edit?name=${encodeURIComponent(
                    userInfo.name
                  )}`
                )
              }
            >
              <Edit className="h-4 w-4 mr-2" />
              프로필 수정
            </Button>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>알림 설정</CardTitle>
            <CardDescription>알림 수신 방법을 설정하세요</CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="flex items-center justify-between">
              <div>
                <p className="font-medium">일정 알림</p>
                <p className="text-sm text-gray-500">모임 및 일정 관련 알림</p>
              </div>
              <Switch
                checked={userInfo.notifications.email}
                onCheckedChange={(checked) =>
                  handleNotificationChange("email", checked)
                }
              />
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>계정 관리</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <Button variant="outline" className="w-full flex justify-start">
              <Shield className="h-4 w-4 mr-2" />
              비밀번호 변경
            </Button>
            <Button
              variant="outline"
              className="w-full flex justify-start text-red-500 hover:text-red-600"
              onClick={() => {
                logout();
                router.push("/login");
              }}
            >
              <LogOut className="h-4 w-4 mr-2" />
              로그아웃
            </Button>
            <Button
              variant="outline"
              className="w-full flex justify-start text-red-500 hover:text-red-600 hover:bg-red-50"
            >
              <User className="h-4 w-4 mr-2" />
              계정 삭제
            </Button>
          </CardContent>
        </Card>
      </main>
    </>
  );
}
