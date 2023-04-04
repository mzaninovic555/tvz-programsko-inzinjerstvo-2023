import api from '../../common/api';
import Post from '../../views/forum/Post';
import ForumResponse from '../../views/forum/ForumResponse';
import ForumPostCreateResponse from '../../views/forum/ForumEntryResponse';


export async function getForumPosts(search: string): Promise<Post[]> {
  const response = await api.get<Post[]>(`/v1/forum?title=${search}`);
  return response.data;
}

export async function getForumPostById(id: string | undefined): Promise<ForumResponse> {
  const response = await api.get<ForumResponse>(`/v1/forum/id/${id}`);
  return response.data;
}

export async function createForumPost(): Promise<ForumPostCreateResponse> {
  const response = await api.get<ForumPostCreateResponse>(`/v1/forum/create`);
  return response.data;
}
