/* 
 * ========================================================
 * 
 * MultiTags.cs 
 * MultiTags v1.0
 * 
 * Created by Aiden De Loryn on 06/05/2015.
 * 
 * ========================================================
 * 
 * - (static) taggedObjects: List<MultiTags>
 * + tags: Tag[]
 * 
 * - (static) GetMultiTagsComponent(GameObject): MultiTags
 * + (static) GameObjectHasTag(GameObject, Tag): bool
 * + (static) TagGameObject(GameObject, Tag): void
 * + (static) UntagGameObject(GameObject, Tag): void
 * + (static) FindGameObjectWithTag(Tag): GameObject
 * + (static) FindGameObjectWithTags(Tag[]): GameObject
 * + (static) FindGameObjectsWithTag(Tag): GameObject[]
 * + (static) FindGameObjectsWithTags(Tag[]): GameObject[]
 * 
 * ========================================================
 * 
 * ========================================================
 */

using UnityEngine;
using System.Collections;
using System.Collections.Generic;

public enum Tag {
	MainCamera, 
	Puck

	// ADD TAGS HERE
}

public class MultiTags : MonoBehaviour {

	static private List<MultiTags> taggedObjects = new List<MultiTags>();
	public Tag[] tags = new Tag[0];

	void Awake () {
		taggedObjects.Add (this);
	}

	void OnDestroy() {
		for (int i = 0; i < taggedObjects.Count; i++) {
			if(taggedObjects[i].Equals(this)) {
				taggedObjects.RemoveAt(i);
				return;
			}
		}
	}

	private static MultiTags GetMultiTagsComponent(GameObject gameObject) {
		MultiTags multiTags = gameObject.GetComponent<MultiTags>();

		if (multiTags == null) {
			multiTags = gameObject.AddComponent<MultiTags>();
		}

		return multiTags;
	}

	public static bool GameObjectHasTag(GameObject gameObject, Tag searchTag) {
		bool hasTag = false;
		MultiTags multiTags = GetMultiTagsComponent(gameObject);

		foreach (Tag objectTag in multiTags.tags) {
			if (objectTag == searchTag) {
				hasTag = true;
				break;
			}
		}

		return hasTag;
	}

	public static void TagGameObject(GameObject gameObject, Tag newTag) {
		MultiTags multiTags = GetMultiTagsComponent(gameObject);
		Tag[] newTags = new Tag[multiTags.tags.Length + 1];
		
		for (int i = 0; i < multiTags.tags.Length; i++) {
			newTags[i] = multiTags.tags[i];
		}
		
		newTags [multiTags.tags.Length] = newTag;
		multiTags.tags = newTags;
	}

	public static void UntagGameObject(GameObject gameObject, Tag target) {
		MultiTags multiTags = GetMultiTagsComponent(gameObject);
		List<Tag> newTags = new List<Tag> (multiTags.tags);

		while (GameObjectHasTag(gameObject, target)) {
			newTags.Remove (target);
			multiTags.tags = newTags.ToArray ();
		}
	}

	public static GameObject FindGameObjectWithTag(Tag searchTag) {
		GameObject[] searchResult = FindGameObjectsWithTag(searchTag);

		if (searchResult.Length > 1) {
			Debug.LogWarning("WARNING: MultiTags.FindGameObjectWithTag(Tag) found more than 1 GameObject.");
		} else if (searchResult.Length < 1) {
			Debug.LogError ("ERROR: MultiTags.FindGameObjectWithTag(Tag) didn't find a GameObject with MultiTag '" + searchTag.ToString() + "'.");
			return new GameObject();
		}

		return searchResult[0];
	}

	public static GameObject FindGameObjectWithTags(Tag[] searchTags) {
		GameObject[] searchResult = FindGameObjectsWithTags(searchTags);
		
		if (searchResult.Length > 1) {
			Debug.LogWarning ("WARNING: MultiTags.FindGameObjectWithTags(Tag[]) found more than 1 GameObject.");
		} else if (searchResult.Length < 1) {
			Debug.LogError ("ERROR: MultiTags.FindGameObjectWithTags(Tag[]) didn't find a GameObject.");
			return new GameObject();
		}

		return searchResult[0];
	}

	public static GameObject[] FindGameObjectsWithTag(Tag searchTag) {
		List<GameObject> searchResult = new List<GameObject> ();

		foreach (MultiTags taggedObject in taggedObjects) {
			for(int i = 0; i < taggedObject.tags.Length; i++) {
				if(taggedObject.tags[i] == searchTag) {
					searchResult.Add(taggedObject.gameObject);
					break;
				}
			}
		}

		return searchResult.ToArray();
	}

	public static GameObject[] FindGameObjectsWithTags(Tag[] searchTags) {
		List<GameObject> searchResult = new List<GameObject> ();
		
		foreach (MultiTags taggedObject in taggedObjects) {
			bool tagsFound = true;

			foreach(Tag searchTag in searchTags) {
				bool tagFound = false;

				foreach(Tag objectTag in taggedObject.tags) {
					if(objectTag == searchTag) {
						tagFound = true;
						break;
					}
				}

				if (!tagFound) {
					tagsFound = false;
					break;
				}
			}

			if (tagsFound) {
				searchResult.Add(taggedObject.gameObject);
			}
		}
		
		return searchResult.ToArray();
	}
}